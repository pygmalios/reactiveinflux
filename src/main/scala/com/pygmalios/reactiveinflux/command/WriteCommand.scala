package com.pygmalios.reactiveinflux.command

import java.time.Instant

import akka.http.scaladsl.model._
import akka.util.ByteString
import com.pygmalios.reactiveinflux.ReactiveInfluxCommand
import com.pygmalios.reactiveinflux.model._
import com.pygmalios.reactiveinflux.response.EmptyJsonResponse

sealed abstract class Precision(val q: String) {
  def format(i: Instant): String
  protected def nano(i: Instant, divideBy: Int, digits: Int): String = {
    val t = i.getNano / divideBy
    if (i.getEpochSecond > 0)
      i.getEpochSecond.toString + t.formatted(s"%0${digits}d")
    else
      t.toString
  }
}
case object Nano extends Precision("n") {
  override def format(i: Instant): String = nano(i, 1, 9)
}
case object Micro extends Precision("u") {
  override def format(i: Instant): String = nano(i, 1000, 6)
}
case object Milli extends Precision("ms") {
  override def format(i: Instant): String = nano(i, 1000000, 3)
}
case object Second extends Precision("s") {
  override def format(i: Instant): String = ???
}
case object Minute extends Precision("m") {
  override def format(i: Instant): String = ???
}
case object Hour extends Precision("h") {
  override def format(i: Instant): String = ???
}

sealed abstract class Consistency(val q: String)
case object One extends Consistency("one")
case object Quorum extends Consistency("quorum")
case object All extends Consistency("all")
case object Any extends Consistency("any")

class WriteCommand(baseUri: Uri,
                   dbName: String,
                   points: Seq[PointNoTime],
                   retentionPolicy: Option[String],
                   username: Option[String],
                   password: Option[String],
                   precision: Option[Precision],
                   consistency: Option[Consistency]) extends ReactiveInfluxCommand {
  import WriteCommand._

  override type TResult = Unit
  protected val uriWithPath = baseUri.withPath(path)
  override protected def responseFactory(httpResponse: HttpResponse) = new WriteResponse(httpResponse)
  override val httpRequest = {
    val pointLines = points.map(pointToLine)
    val lines = HttpEntity.Strict(ContentTypes.`application/octet-stream`, ByteString(pointLines.mkString("\n")))
    HttpRequest(
      method  = HttpMethods.POST,
      uri     = uriWithPath.withQuery(query),
      entity  = lines
    )
  }

  private def query = {
    val qMap = Map(
      dbQ -> Some(dbName),
      retentionPolicyQ -> retentionPolicy,
      usernameQ -> username,
      passwordQ -> password,
      precisionQ -> precision.map(_.q),
      consistencyQ -> consistency.map(_.q)
    ).collect {
      case (k, Some(v)) => k -> v
    }

    Uri.Query(qMap)
  }

  private def pointToLine(point: PointNoTime): String = {
    val sb = new StringBuilder

    // Measurement
    sb.append(point.measurement)

    // Tags
    point.tags.foreach { tag =>
      sb.append(",")
      sb.append(tag._1)
      sb.append("=")
      sb.append(tag._2)
    }

    // Fields
    if (point.fields.nonEmpty) {
      sb.append(" ")
      val fieldStrings = point.fields.map { field =>
        field._1 + "=" + fieldValueToLine(field._2)
      }

      sb.append(fieldStrings.mkString(","))
    }

    // Timestamp
    val prec = precision.getOrElse(Nano)
    point match {
      case pointWithTime: Point =>
        sb.append(" ")
        sb.append(prec.format(pointWithTime.time))
    }

    sb.toString()
  }

  private def fieldValueToLine(fieldValue: FieldValue): String = fieldValue match {
    case StringFieldValue(v) => v
    case FloatFieldValue(v) => v.toString
    case LongFieldValue(v) => v.toString + "i"
    case BooleanFieldValue(v) => v.toString
  }
}

object WriteCommand {
  val path = Uri.Path("/write")
  val dbQ = "db"
  val retentionPolicyQ = "rp"
  val usernameQ = "u"
  val passwordQ = "p"
  val precisionQ = "precision"
  val consistencyQ = "consistency"
}

private[reactiveinflux] class WriteResponse(httpResponse: HttpResponse) extends EmptyJsonResponse(httpResponse)