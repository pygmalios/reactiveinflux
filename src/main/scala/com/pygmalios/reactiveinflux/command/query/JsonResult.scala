package com.pygmalios.reactiveinflux.command.query

import play.api.libs.json.{JsValue, Json}

case class JsonResult(series: List[JsonSeries])
case class JsonSeries(name: String, columns: List[String], values: List[List[JsValue]])

object JsonResultFormat {
  implicit val jsonSeriesFormat = Json.format[JsonSeries]
  implicit val jsonResultFormat = Json.format[JsonResult]
}
