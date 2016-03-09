package com.pygmalios.reactiveinflux.command.query

import play.api.libs.json.{JsValue, Json}

private[reactiveinflux] case class JsonResult(series: List[JsonSeries])
private[reactiveinflux] case class JsonSeries(name: String, columns: List[String], values: List[List[JsValue]])

private[reactiveinflux] object JsonResultFormat {
  implicit val jsonSeriesFormat = Json.format[JsonSeries]
  implicit val jsonResultFormat = Json.format[JsonResult]
}
