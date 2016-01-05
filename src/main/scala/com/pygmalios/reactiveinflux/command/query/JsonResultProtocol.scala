package com.pygmalios.reactiveinflux.command.query

import spray.json.{DefaultJsonProtocol, JsValue}

private[reactiveinflux] case class JsonResult(series: List[JsonSeries])
private[reactiveinflux] case class JsonSeries(name: String, columns: List[String], values: List[List[JsValue]])
private[reactiveinflux] object JsonResultProtocol extends DefaultJsonProtocol {
  implicit val jsonSeriesFormat = jsonFormat3(JsonSeries)
  implicit val jsonResultFormat = jsonFormat1(JsonResult)
}
