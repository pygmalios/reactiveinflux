package com.pygmalios.reactiveinflux.api

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import com.pygmalios.reactiveinflux.impl.response.ReactiveinfluxJsonResultException

import scala.concurrent.Future

private[reactiveinflux] trait ReactiveinfluxCoreClient {
  def execute[R <: ReactiveinfluxRequest](request: R): Future[request.TResponse]
}

private[reactiveinflux] trait ReactiveinfluxRequest extends Serializable {
  type TResponse <: Any

  def httpRequest: HttpRequest

  def apply(httpResponse: HttpResponse): TResponse = {
    try {
      responseFactory(httpResponse).result
    }
    catch {
      case ex: ReactiveinfluxJsonResultException =>
        throw new ReactiveinfluxResultError(ex.errors, httpRequest)
      case ex: Exception =>
        throw new ReactiveinfluxException(s"Response processing failed! [${httpRequest.method.name} ${httpRequest.uri}]", ex)
    }
  }

  protected def responseFactory(httpResponse: HttpResponse): ReactiveinfluxResponse[TResponse]
}

private[reactiveinflux] trait ReactiveinfluxResponse[+T] {
  def result: T
}
