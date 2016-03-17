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

  /**
    * Build HTTP request.
    */
  def httpRequest(ws: WSClient): WSRequestHolder

  /**
    * Create result from HTTP response.
    */
  def apply(wsRequest: WSRequestHolder, wsResponse: WSResponse): TResult = {
    try {
      responseFactory(wsResponse).result
    }
    catch {
      case ex: ReactiveInfluxJsonResultException =>
        throw new ReactiveInfluxResultError(ex.errors, wsRequest)
      case ex: Exception =>
        throw new ReactiveInfluxException(s"Response processing failed!\n  [$wsResponse]\n  [${wsRequest.method}]\n  [${wsRequest.url}\n  [${wsResponse.body}]]", ex)
    }
  }

  protected def responseFactory(httpResponse: WSResponse): ReactiveInfluxResult[TResult]
}

trait ReactiveInfluxResult[+T] extends Serializable {
  def result: T
}