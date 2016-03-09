package com.pygmalios.reactiveinflux

import com.pygmalios.reactiveinflux.error.ReactiveInfluxError
import play.api.libs.ws.WSRequestHolder

class ReactiveInfluxException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)
class ReactiveInfluxResultError(val errors: Set[ReactiveInfluxError], val request: WSRequestHolder)
  extends ReactiveInfluxException(s"${errors.mkString(",")} [${request.method} ${request.url}]")
