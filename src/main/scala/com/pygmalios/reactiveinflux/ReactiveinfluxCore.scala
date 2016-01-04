package com.pygmalios.reactiveinflux

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import com.pygmalios.reactiveinflux.response.ReactiveInfluxJsonResultException

import scala.concurrent.Future

trait ReactiveInfluxCore {
  def config: ReactiveInfluxConfig
  def execute[R <: ReactiveInfluxCommand](command: R): Future[command.TResult]
}

trait ReactiveInfluxCommand extends Serializable {
  type TResult <: Any

  def httpRequest: HttpRequest

  def apply(httpResponse: HttpResponse): TResult = {
    try {
      responseFactory(httpResponse).result
    }
    catch {
      case ex: ReactiveInfluxJsonResultException =>
        throw new ReactiveInfluxResultError(ex.errors, httpRequest)
      case ex: Exception =>
        throw new ReactiveInfluxException(s"Response processing failed!\n  [$httpResponse]\n  [${httpRequest.method.name}]\n  [${httpRequest.uri}]", ex)
    }
  }

  protected def responseFactory(httpResponse: HttpResponse): ReactiveInfluxResult[TResult]
}

trait ReactiveInfluxResult[+T] {
  def result: T
}