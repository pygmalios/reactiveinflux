package com.pygmalios.reactiveinflux.command.query

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import com.pygmalios.reactiveinflux.ReactiveInfluxException
import com.pygmalios.reactiveinflux.response.JsonResponse
import spray.json.{DefaultJsonProtocol, JsArray, JsObject, JsValue}

class QueryCommand(baseUri: Uri, qs: Seq[Query], params: QueryParameters) extends BaseQueryCommand(baseUri) {
  override type TResult = Seq[QueryResult]
  override protected def responseFactory(httpResponse: HttpResponse) = new QueryCommandResult(httpResponse, qs)
  override val httpRequest = HttpRequest(uri = qUri(qs.map(_.influxQl).mkString(";")))
}

private[reactiveinflux] class QueryCommandResult(httpResponse: HttpResponse, qs: Seq[Query])
  extends JsonResponse[Seq[QueryResult]](httpResponse) {
  override def result: Seq[QueryResult] = qs.zip(results.elements).map { case (q, jsResult) =>
    QueryResult(q, jsToResult(jsResult.asJsObject))
  }

  private[reactiveinflux] def jsToResult(jsResult: JsObject): Result =
    jsResult.fields.get(QueryCommandResult.seriesField) match {
      case Some(series: JsArray) => Result(series.elements.map(jsToSeries))
      case Some(other) => throw new ReactiveInfluxException("Series field is not an array!")
      case None => throw new ReactiveInfluxException("Series field not found!")
    }

  private[reactiveinflux] def jsToSeries(jsSeries: JsValue): Series = {
    ???
  }
}

object QueryCommandResult {
  val seriesField = "series"
}

private case class JsonResult(series: List[JsonSeries])
private case class JsonSeries(name: String, columns: List[String], values: List[List[JsValue]])
private object JsonResultProtocol extends DefaultJsonProtocol {
  implicit val jsonSeriesFormat = jsonFormat3(JsonSeries)
  implicit val jsonResultFormat = jsonFormat1(JsonResult)
}