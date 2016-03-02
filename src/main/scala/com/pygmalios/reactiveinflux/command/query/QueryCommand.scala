package com.pygmalios.reactiveinflux.command.query

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import com.pygmalios.reactiveinflux.ReactiveInflux._
import com.pygmalios.reactiveinflux.ReactiveInfluxException
import com.pygmalios.reactiveinflux.impl.OptionalParameters
import com.pygmalios.reactiveinflux.response.JsonResponse
import spray.json._

class QueryCommand(baseUri: Uri, dbName: DbName, qs: Seq[Query], params: QueryParameters) extends BaseQueryCommand(baseUri) {
  override type TResult = Seq[QueryResult]
  override protected def responseFactory(httpResponse: HttpResponse, actorSystem: ActorSystem) = {
    val timeFormat: TimeFormat = params.epoch.getOrElse(Rfc3339)
    new QueryCommandResult(httpResponse, qs, timeFormat, actorSystem)
  }
  override val httpRequest = {
    val q = qs.map(_.influxQl).mkString(";")
    HttpRequest(uri = qUri(q))
  }
  override def otherParams = OptionalParameters(
    QueryParameters.dbQ -> Some(dbName),
    QueryParameters.epochQ -> params.epoch.map(_.q),
    QueryParameters.chunkSizeQ -> params.chunkSize.map(_.toString)
  )
}

private[reactiveinflux] class QueryCommandResult(httpResponse: HttpResponse, qs: Seq[Query], timeFormat: TimeFormat, actorSystem: ActorSystem)
  extends JsonResponse[Seq[QueryResult]](httpResponse, actorSystem) {
  import JsonResultProtocol._

  override def result: Seq[QueryResult] = qs.zip(results.elements).map { case (q, jsResult) =>
    QueryResult(q, jsToResult(jsResult.convertTo[JsonResult]))
  }

  private def jsToResult(jsonResult: JsonResult): Result = Result(jsonResult.series.map(jsToSeries))

  private def jsToSeries(jsonSeries: JsonSeries): Series = Series(
    name        = jsonSeries.name,
    columns     = jsonSeries.columns,
    values      = jsonSeries.values.map(jsRow => jsRow.map(jsToValue)),
    timeFormat  = timeFormat
  )

  private def jsToValue(jsValue: JsValue): Value = jsValue match {
    case JsString(value) => StringValue(value)
    case JsNumber(value) => BigDecimalValue(value)
    case JsBoolean(value) => BooleanValue(value)
    case other => throw new ReactiveInfluxException(s"Unsupported JSON value type! [$other]")
  }
}

object QueryCommandResult {
  val seriesField = "series"
}