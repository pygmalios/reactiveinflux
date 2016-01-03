package com.pygmalios.reactiveinflux.core

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import com.pygmalios.reactiveinflux.api.{ReactiveinfluxException, ReactiveinfluxResultError}
import com.pygmalios.reactiveinflux.impl.response.ReactiveinfluxJsonResultException

import scala.concurrent.Future

trait ReactiveinfluxRequest extends Serializable {
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

trait ReactiveinfluxResponse[+T] {
  def result: T
}

trait ReactiveinfluxCoreClient {
  def execute[R <: ReactiveinfluxRequest](request: R): Future[request.TResponse]
}
