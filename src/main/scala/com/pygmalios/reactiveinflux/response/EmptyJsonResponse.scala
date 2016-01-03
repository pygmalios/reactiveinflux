package com.pygmalios.reactiveinflux.response

import akka.http.scaladsl.model.HttpResponse

class EmptyJsonResponse(httpResponse: HttpResponse) extends JsonResponse[Unit](httpResponse) {
  val result = ()
}