package com.pygmalios.reactiveinflux

import com.pygmalios.reactiveinflux.response.ReactiveInfluxJsonResultException
import play.api.libs.ws.{WSClient, WSRequestHolder, WSResponse}

import scala.concurrent.Future

trait ReactiveInfluxCore {
  def config: ReactiveInfluxConfig
  def execute[R <: ReactiveInfluxCommand](command: R): Future[command.TResult]
}

trait ReactiveInfluxCommand extends Serializable {
  type TResult <: Any

  def httpRequest(ws: WSClient): WSRequestHolder

  def apply(wsRequest: WSRequestHolder, httpResponse: WSResponse): TResult = {
    try {
      responseFactory(httpResponse).result
    }
    catch {
      case ex: ReactiveInfluxJsonResultException =>
        throw new ReactiveInfluxResultError(ex.errors, wsRequest)
      case ex: Exception =>
        throw new ReactiveInfluxException(s"Response processing failed!\n  [$httpResponse]\n  [${wsRequest.method}]\n  [${wsRequest.url}]", ex)
    }
  }

  protected def responseFactory(httpResponse: WSResponse): ReactiveInfluxResult[TResult]
}

trait ReactiveInfluxResult[+T] extends Serializable {
  def result: T
}