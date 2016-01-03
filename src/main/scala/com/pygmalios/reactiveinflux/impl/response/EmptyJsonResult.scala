package com.pygmalios.reactiveinflux.impl.response

import akka.http.scaladsl.model.HttpResponse

class EmptyJsonResult(httpResponse: HttpResponse) extends JsonResult[Unit](httpResponse) {
  val result = ()
}