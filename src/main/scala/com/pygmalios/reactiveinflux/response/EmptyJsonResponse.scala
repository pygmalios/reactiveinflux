package com.pygmalios.reactiveinflux.response

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse

class EmptyJsonResponse(httpResponse: HttpResponse, actorSystem: ActorSystem)
  extends JsonResponse[Unit](httpResponse, actorSystem) {
  val result = ()
}