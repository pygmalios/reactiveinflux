package com.pygmalios.reactiveinflux.core

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Future

trait ReactiveinfluxRequest extends Serializable {
  type Response <: Any
  def httpRequest: HttpRequest
  def apply(httpResponse: HttpResponse): Response
}

trait ReactiveinfluxCoreClient {
  def execute[R <: ReactiveinfluxRequest](request: R): Future[request.Response]
}
