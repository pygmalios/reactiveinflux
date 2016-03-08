package com.pygmalios.reactiveinflux.response

import com.pygmalios.reactiveinflux.error.ReactiveInfluxError
import com.pygmalios.reactiveinflux.{ReactiveInfluxException, ReactiveInfluxResult}
import play.api.libs.json.JsArray
import play.api.libs.ws.WSResponse

class ReactiveInfluxJsonResultException(val errors: Set[ReactiveInfluxError]) extends ReactiveInfluxException(errors.mkString(","))

abstract class PlayWSJsonResponse[+T](wsResponse: WSResponse) extends ReactiveInfluxResult[T] {
  // TODO: ...
  protected val results: JsArray = ???
  protected def errorHandler: PartialFunction[ReactiveInfluxError, Option[ReactiveInfluxError]] = PartialFunction.empty
}
