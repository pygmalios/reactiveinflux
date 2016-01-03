package com.pygmalios.reactiveinflux

import akka.http.scaladsl.model.HttpRequest
import com.pygmalios.reactiveinflux.error.ReactiveInfluxError

class ReactiveInfluxException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)
class ReactiveInfluxResultError(val errors: Set[ReactiveInfluxError], val request: HttpRequest)
  extends ReactiveInfluxException(s"${errors.mkString(",")} [${request.method.name} ${request.uri}]")
