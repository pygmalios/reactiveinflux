package com.pygmalios.reactiveinflux.api

import akka.http.scaladsl.model.HttpRequest
import com.pygmalios.reactiveinflux.api.response.errors.ReactiveinfluxError

class ReactiveinfluxException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)
class ReactiveinfluxResultError(val errors: Set[ReactiveinfluxError], val request: HttpRequest)
  extends ReactiveinfluxException(s"${errors.mkString(",")} [${request.method.name} ${request.uri}]")
