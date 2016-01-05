package com.pygmalios.reactiveinflux.command.query

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import com.pygmalios.reactiveinflux.ReactiveInfluxException
import com.pygmalios.reactiveinflux.response.JsonResponse
import spray.json._

class QueryCommand(baseUri: Uri, qs: Seq[Query], params: QueryParameters) extends BaseQueryCommand(baseUri) {
  override type TResult = Seq[QueryResult]
  override protected def responseFactory(httpResponse: HttpResponse) = {
    val timeFormat: TimeFormat = params.epoch.getOrElse(Rfc3339)
    new QueryCommandResult(httpResponse, qs, timeFormat)
  }
  override val httpRequest = HttpRequest(uri = qUri(qs.map(_.influxQl).mkString(";")))
}

private[reactiveinflux] class QueryCommandResult(httpResponse: HttpResponse, qs: Seq[Query], timeFormat: TimeFormat)
  extends JsonResponse[Seq[QueryResult]](httpResponse) {
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