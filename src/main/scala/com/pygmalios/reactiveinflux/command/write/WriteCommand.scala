package com.pygmalios.reactiveinflux.command.write

import java.net.URI

import com.pygmalios.reactiveinflux.Point.{FieldKey, TagKey, TagValue}
import com.pygmalios.reactiveinflux._
import com.pygmalios.reactiveinflux.impl.URIUtils
import com.pygmalios.reactiveinflux.response.EmptyJsonResponse
import org.slf4j.LoggerFactory
import play.api.http.{HeaderNames, HttpVerbs, MimeTypes}
import play.api.libs.ws.{WSClient, WSRequestHolder, WSResponse}

class WriteCommand(val baseUri: URI,
                   val dbName: ReactiveInfluxDbName,
                   val points: Iterable[PointNoTime],
                   val params: WriteParameters) extends ReactiveInfluxCommand {
  import WriteCommand._

  override type TResult = Unit
  protected val uriWithPath = URIUtils.appendPath(baseUri, path)
  override protected def responseFactory(wsResponse: WSResponse) = new WriteResponse(wsResponse)
  override def httpRequest(ws: WSClient): WSRequestHolder = {
    val completeQuery = query.filter(_._2.isDefined).mapValues(_.get)
    val uri = URIUtils.appendQuery(uriWithPath, completeQuery.toVector:_*).toString
    val result = ws
      .url(uri)
      .withHeaders(HeaderNames.CONTENT_TYPE -> MimeTypes.BINARY)
      .withMethod(HttpVerbs.POST)
      .withBody(new WriteLines(points, prec).toString)

    result
  }

  override def logInfo = {
    val p = Vector(params.retentionPolicy, params.consistency, params.precision).flatten.mkString(",")
    s"Points=${points.size} Parameters=$p"
  }

  private[command] def prec: Precision = params.precision.getOrElse(Nano)

  private[command] def query: Map[String, Option[String]] = {
    Map(
      dbQ -> Some(dbName.value)
    ) ++ params.params.mapValues(Some(_))
  }

  override def toString = s"WriteCommand(uriWithPath=$uriWithPath, baseUri=$baseUri, dbName=${dbName.value}, points=$points, params=$params)"

  def canEqual(other: Any): Boolean = other.isInstanceOf[WriteCommand]

  override def equals(other: Any): Boolean = other match {
    case that: WriteCommand =>
      (that canEqual this) &&
        baseUri == that.baseUri &&
        dbName == that.dbName &&
        points == that.points &&
        params == that.params
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(baseUri, dbName, points, params)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object WriteCommand {
  val path = "/write"
  val dbQ = "db"

  private val log = LoggerFactory.getLogger(classOf[WriteCommand])
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