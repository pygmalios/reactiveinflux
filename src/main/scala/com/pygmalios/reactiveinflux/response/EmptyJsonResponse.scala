package com.pygmalios.reactiveinflux.response

import play.api.libs.ws.WSResponse

class EmptyJsonResponse(wsResponse: WSResponse) extends PlayWSJsonResponse[Unit](wsResponse) {
  val result = ()
}