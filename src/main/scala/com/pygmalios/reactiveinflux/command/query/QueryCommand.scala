package com.pygmalios.reactiveinflux.command.query

import java.net.URI

import com.pygmalios.reactiveinflux.ReactiveInflux._
import com.pygmalios.reactiveinflux.impl.OptionalParameters
import com.pygmalios.reactiveinflux.response.PlayWSJsonResponse
import play.api.libs.ws.{WSClient, WSResponse}

class QueryCommand(baseUri: URI, dbName: DbName, qs: Seq[Query], params: QueryParameters) extends BaseQueryCommand(baseUri) {
  override type TResult = Seq[QueryResult]
  override protected def responseFactory(wsResponse: WSResponse) = {
    val timeFormat: TimeFormat = params.epoch.getOrElse(Rfc3339)
    new QueryCommandResult(wsResponse, qs, timeFormat)
  }
  override def httpRequest(ws: WSClient) = {
    val q = qs.map(_.influxQl).mkString(";")
    ws.url(qUri(q).toString)
  }
  override def otherParams = OptionalParameters(
    QueryParameters.dbQ -> Some(dbName),
    QueryParameters.epochQ -> params.epoch.map(_.q),
    QueryParameters.chunkSizeQ -> params.chunkSize.map(_.toString)
  )
}

private[reactiveinflux] class QueryCommandResult(wsResponse: WSResponse, qs: Seq[Query], timeFormat: TimeFormat)
  extends PlayWSJsonResponse[Seq[QueryResult]](wsResponse) {

  // TODO: ...
  /*
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
  }*/
  override def result: Seq[QueryResult] = ???
}

object QueryCommandResult {
  val seriesField = "series"
}