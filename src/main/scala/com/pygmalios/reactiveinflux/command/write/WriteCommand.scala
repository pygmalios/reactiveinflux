package com.pygmalios.reactiveinflux.command.write

import java.net.URI

import com.pygmalios.reactiveinflux.ReactiveInflux.{DbName, DbPassword, DbUsername}
import com.pygmalios.reactiveinflux.ReactiveInfluxCommand
import com.pygmalios.reactiveinflux.command.write.Point.{FieldKey, TagKey, TagValue}
import com.pygmalios.reactiveinflux.impl.URIUtils
import com.pygmalios.reactiveinflux.response.EmptyJsonResponse
import play.api.libs.ws.{WSClient, WSResponse}

class WriteCommand(val baseUri: URI,
                   val dbName: DbName,
                   val dbUsername: Option[DbUsername],
                   val dbPassword: Option[DbPassword],
                   val points: Iterable[PointNoTime],
                   val params: WriteParameters) extends ReactiveInfluxCommand {
  import WriteCommand._

  override type TResult = Unit
  protected val uriWithPath = new URI(baseUri.toString + path)
  override protected def responseFactory(wsResponse: WSResponse) = new WriteResponse(wsResponse)
  override def httpRequest(ws: WSClient) =
    ws
      .url(new URI(uriWithPath.toString + URIUtils.queryToString(query)).toString)
      .withHeaders("Content-Type" -> "application/octet-stream")
      .withMethod("POST")
      .withBody(new WriteLines(points, prec).toString)

  private[command] def prec: Precision = params.precision.getOrElse(Nano)

  private[command] def query: Map[String, Option[String]] = Map(
    dbQ -> Some(dbName),
    usernameQ -> dbUsername,
    passwordQ -> dbPassword
  )

  override def toString = s"WriteCommand(uriWithPath=$uriWithPath, baseUri=$baseUri, dbName=$dbName, dbUsername=$dbUsername, dbPassword=$dbPassword, points=$points, params=$params)"

  def canEqual(other: Any): Boolean = other.isInstanceOf[WriteCommand]

  override def equals(other: Any): Boolean = other match {
    case that: WriteCommand =>
      (that canEqual this) &&
        baseUri == that.baseUri &&
        dbName == that.dbName &&
        dbUsername == that.dbUsername &&
        dbPassword == that.dbPassword &&
        points == that.points &&
        params == that.params
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(baseUri, dbName, dbUsername, dbPassword, points, params)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object WriteCommand {
  val path = "/write"
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
    case BigDecimalFieldValue(v) => v.toString
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

private[reactiveinflux] class WriteResponse(wsResponse: WSResponse)
  extends EmptyJsonResponse(wsResponse)