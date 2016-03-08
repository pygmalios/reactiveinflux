package com.pygmalios.reactiveinflux

import com.pygmalios.reactiveinflux.response.ReactiveInfluxJsonResultException
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.Future

trait ReactiveInfluxCore {
  def config: ReactiveInfluxConfig
  def execute[R <: ReactiveInfluxCommand](command: R): Future[command.TResult]
}

trait ReactiveInfluxCommand extends Serializable {
  type TResult <: Any

  def httpRequest(ws: WSClient): WSRequest

  def apply(httpRequest: WSRequest, httpResponse: WSResponse): TResult = {
    try {
      responseFactory(httpResponse).result
    }
    catch {
      case ex: ReactiveInfluxJsonResultException =>
        throw new ReactiveInfluxResultError(ex.errors, httpRequest)
      case ex: Exception =>
        throw new ReactiveInfluxException(s"Response processing failed!\n  [$httpResponse]\n  [${httpRequest.method}]\n  [${httpRequest.uri}]", ex)
    }
  }

  protected def responseFactory(httpResponse: WSResponse): ReactiveInfluxResult[TResult]
}

trait ReactiveInfluxResult[+T] extends Serializable {
  def result: T
}