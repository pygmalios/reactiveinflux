package com.pygmalios.reactiveinflux.impl.query

import akka.http.scaladsl.model.Uri
import com.pygmalios.reactiveinflux.core.ReactiveinfluxRequest
import com.pygmalios.reactiveinflux.impl.Logging

abstract class BaseQuery extends ReactiveinfluxRequest with Logging {
  val queryKeys = Set("q")
  val path = Uri.Path("/query")
}
