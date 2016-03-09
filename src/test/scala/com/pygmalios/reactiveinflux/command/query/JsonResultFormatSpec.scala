package com.pygmalios.reactiveinflux.command.query

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import play.api.libs.json.{JsNumber, JsString, Json}

@RunWith(classOf[JUnitRunner])
class JsonResultFormatSpec extends FlatSpec {
  import JsonResultFormatSpec._
  import JsonResultFormat._

  behavior of "protocol"

  it should "parse result with one series" in {
    // Execute
    val jsonResult = Json.parse(example1).as[JsonResult]

    assert(jsonResult.series.size == 1)
    val series = jsonResult.series.head
    assert(series.name == "cpu_load_short")
    assert(series.columns == List("time", "value"))
    assert(series.values.size == 3)
    assert(series.values(0)(0) == JsString("2015-01-29T21:55:43.702900257Z"))
    assert(series.values(0)(1) == JsNumber(0.55))
    assert(series.values(1)(0) == JsString("2015-01-29T21:55:43.702900257Z"))
    assert(series.values(1)(1) == JsNumber(23422))
    assert(series.values(2)(0) == JsString("2015-06-11T20:46:02Z"))
    assert(series.values(2)(1) == JsNumber(0.64))
  }
}

object JsonResultFormatSpec {
  val example1 = """
                   |{
                   |            "series": [
                   |                {
                   |                    "name": "cpu_load_short",
                   |                    "columns": [
                   |                        "time",
                   |                        "value"
                   |                    ],
                   |                    "values": [
                   |                        [
                   |                            "2015-01-29T21:55:43.702900257Z",
                   |                            0.55
                   |                        ],
                   |                        [
                   |                            "2015-01-29T21:55:43.702900257Z",
                   |                            23422
                   |                        ],
                   |                        [
                   |                            "2015-06-11T20:46:02Z",
                   |                            0.64
                   |                        ]
                   |                    ]
                   |                }
                   |            ]
                   |        }
                 """.stripMargin
}