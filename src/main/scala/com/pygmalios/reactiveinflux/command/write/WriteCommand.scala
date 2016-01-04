package com.pygmalios.reactiveinflux.command.write

import akka.http.scaladsl.model._
import akka.util.ByteString
import com.pygmalios.reactiveinflux.ReactiveInfluxCommand
import com.pygmalios.reactiveinflux.command.{Nano, Precision}
import com.pygmalios.reactiveinflux.model.Point.{FieldKey, TagKey, TagValue}
import com.pygmalios.reactiveinflux.model._
import com.pygmalios.reactiveinflux.response.EmptyJsonResponse

class WriteCommand(val baseUri: Uri,
                   val dbName: String,
                   val dbUsername: Option[String],
                   val dbPassword: Option[String],
                   val points: Iterable[PointNoTime],
                   val params: WriteParameters) extends ReactiveInfluxCommand {
  import WriteCommand._

  override type TResult = Unit
  protected val uriWithPath = baseUri.withPath(path)
  override protected def responseFactory(httpResponse: HttpResponse) = new WriteResponse(httpResponse)
  override val httpRequest = {
    val lines = new WriteLines(points, prec).toString
    val entity = HttpEntity.Strict(ContentTypes.`application/octet-stream`, ByteString(lines))
    HttpRequest(
      method  = HttpMethods.POST,
      uri     = uriWithPath.withQuery(query),
      entity  = entity
    )
  }

  private[command] def prec: Precision = params.precision.getOrElse(Nano)

  private[command] def query: Uri.Query = {
    val qMap = OptionalParameters(
      dbQ -> Some(dbName),
      usernameQ -> dbUsername,
      passwordQ -> dbPassword
    )

    Uri.Query(qMap ++ params.params)
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[WriteCommand]

  override def equals(other: Any): Boolean = other match {
    case that: WriteCommand =>
      (that canEqual this) &&
        httpRequest == that.httpRequest
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(httpRequest)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString = s"WriteCommand(uriWithPath=$uriWithPath, httpRequest=$httpRequest, baseUri=$baseUri, dbName=$dbName, dbUsername=$dbUsername, dbPassword=$dbPassword, points=$points, params=$params)"
}

object WriteCommand {
  val path = Uri.Path("/write")
  val dbQ = "db"
  val usernameQ = "u"
  val passwordQ = "p"
}

private[reactiveinflux] class WriteLines(points: Iterable[PointNoTime], precision: Precision) {
  override def toString: String = {
    val sb = new StringBuilder
    points.foreach { point =>
      if (sb.nonEmpty)
        sb.append("\n")
      pointToLine(point, precision, sb)
    }
    sb.toString()
  }

  private[command] def pointToLine(point: PointNoTime, precision: Precision, sb: StringBuilder): Unit = {
    sb.append(point.measurement.escaped)
    tagsToLine(point.tags, sb)
    fieldsToLine(point.fields, sb)
    timestampToLine(point, precision, sb)
  }

  private[command] def tagsToLine(tags: Map[TagKey, TagValue], sb: StringBuilder): Unit = {
    tags.foreach { tag =>
      sb.append(",")
      sb.append(tag._1.escaped)
      sb.append("=")
      sb.append(tag._2.escaped)
    }
  }

  private[command] def fieldsToLine(fields: Map[FieldKey, FieldValue], sb: StringBuilder): Unit = {
    if (fields.nonEmpty) {
      sb.append(" ")
      val fieldStrings = fields.map { field =>
        field._1.escaped + "=" + fieldValueToLine(field._2)
      }

      sb.append(fieldStrings.mkString(","))
    }
  }

  private[command] def fieldValueToLine(fieldValue: FieldValue): String = fieldValue match {
    case StringFieldValue(v) =>  "\"" + v.replace("\"", "\\\"") + "\""
    case DoubleFieldValue(v) => v.toString
    case LongFieldValue(v) => v.toString + "i"
    case BooleanFieldValue(v) => v.toString
  }

  private[command] def timestampToLine(point: PointNoTime, precision: Precision, sb: StringBuilder): Unit = {
    point match {
      case pointWithTime: Point =>
        sb.append(" ")
        sb.append(precision.format(pointWithTime.time))
      case _ => // No timestamp provided
    }
  }
}

private[reactiveinflux] class WriteResponse(httpResponse: HttpResponse) extends EmptyJsonResponse(httpResponse)