package com.pygmalios.reactiveinflux.command

import akka.http.scaladsl.model._
import akka.util.ByteString
import com.pygmalios.reactiveinflux.ReactiveInfluxCommand
import com.pygmalios.reactiveinflux.model.Point.{FieldKey, TagKey, TagValue}
import com.pygmalios.reactiveinflux.model._
import com.pygmalios.reactiveinflux.response.EmptyJsonResponse

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
    val entity = HttpEntity.Strict(ContentTypes.`application/octet-stream`, ByteString(pointsToLine(points)))
    HttpRequest(
      method  = HttpMethods.POST,
      uri     = uriWithPath.withQuery(query),
      entity  = entity
    )
  }

  private def prec: Precision = precision.getOrElse(Nano)

  private def query = {
    val qMap = Map(
      dbQ -> Some(dbName),
      retentionPolicyQ -> retentionPolicy,
      usernameQ -> username,
      passwordQ -> password,
      precisionQ -> Some(prec.q),
      consistencyQ -> consistency.map(_.q)
    ).collect {
      case (k, Some(v)) => k -> v
    }

    Uri.Query(qMap)
  }

  private def pointsToLine(points: Seq[PointNoTime]): String = {
    val sb = new StringBuilder
    points.foreach { point =>
      pointToLine(point, sb)
      sb.append("\n")
    }
    sb.toString()
  }

  private def pointToLine(point: PointNoTime, sb: StringBuilder): Unit = {
    sb.append(point.measurement)
    tagsToLine(point.tags, sb)
    fieldsToLine(point.fields, sb)
    timestampToLine(point, sb)
  }

  private def tagsToLine(tags: Map[TagKey, TagValue], sb: StringBuilder): Unit = {
    tags.foreach { tag =>
      sb.append(",")
      sb.append(tag._1)
      sb.append("=")
      sb.append(tag._2)
    }
  }

  private def fieldsToLine(fields: Map[FieldKey, FieldValue], sb: StringBuilder): Unit = {
    if (fields.nonEmpty) {
      sb.append(" ")
      val fieldStrings = fields.map { field =>
        field._1 + "=" + fieldValueToLine(field._2)
      }

      sb.append(fieldStrings.mkString(","))
    }
  }

  private def fieldValueToLine(fieldValue: FieldValue): String = fieldValue match {
    case StringFieldValue(v) => v
    case FloatFieldValue(v) => v.toString
    case LongFieldValue(v) => v.toString + "i"
    case BooleanFieldValue(v) => v.toString
  }

  private def timestampToLine(point: PointNoTime, sb: StringBuilder): Unit = {
    point match {
      case pointWithTime: Point =>
        sb.append(" ")
        sb.append(prec.format(pointWithTime.time))
      case _ => // No timestamp provided
    }
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