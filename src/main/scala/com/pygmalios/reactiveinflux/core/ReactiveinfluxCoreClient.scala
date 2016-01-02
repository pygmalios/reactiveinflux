package com.pygmalios.reactiveinflux.core

import akka.http.scaladsl.model.{HttpResponse, HttpRequest, Uri}

import scala.concurrent.Future

trait ReactiveinfluxRequest {
  type Response <: Any
  def httpRequest: HttpRequest
  def apply(httpResponse: HttpResponse): Response

  def path: Uri.Path
  def queryKeys: Set[String]
}

trait ReactiveinfluxCoreClient {
  def execute[R <: ReactiveinfluxRequest](request: R): Future[request.Response]
}
