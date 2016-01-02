package com.pygmalios.reactiveinflux.core

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import com.pygmalios.reactiveinflux.api.{ReactiveinfluxException, ReactiveinfluxResultError}
import com.pygmalios.reactiveinflux.impl.response.{JsonResponse, ReactiveinfluxJsonResultException}

import scala.concurrent.Future

trait ReactiveinfluxRequest extends Serializable {
  type Response <: Any

  def httpRequest: HttpRequest

  def apply(httpResponse: HttpResponse): Response = try {
    responseFactory(httpResponse).result
  }
  catch {
    case ex: ReactiveinfluxJsonResultException =>
      throw new ReactiveinfluxResultError(ex.error, httpRequest)
    case ex: Exception =>
      throw new ReactiveinfluxException(s"Response processing failed! [${httpRequest.method.name} ${httpRequest.uri}]", ex)
  }

  protected def responseFactory: (HttpResponse) => JsonResponse[Response]}

trait ReactiveinfluxCoreClient {
  def execute[R <: ReactiveinfluxRequest](request: R): Future[request.Response]
}
