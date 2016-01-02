package com.pygmalios.reactiveinflux.impl.response

import akka.http.scaladsl.model.HttpResponse

class EmptyJsonResponse(httpResponse: HttpResponse) extends JsonResponse[Unit](httpResponse) {
  val result = ()
}
