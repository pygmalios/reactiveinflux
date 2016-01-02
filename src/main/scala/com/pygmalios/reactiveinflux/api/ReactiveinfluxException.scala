package com.pygmalios.reactiveinflux.api

import akka.http.scaladsl.model.HttpRequest

class ReactiveinfluxException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)
class ReactiveinfluxResultError(error: String, request: HttpRequest) extends ReactiveinfluxException(s"$error [${request.method.name} ${request.uri}]")
