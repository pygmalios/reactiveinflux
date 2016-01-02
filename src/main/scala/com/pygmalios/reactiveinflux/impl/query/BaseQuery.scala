package com.pygmalios.reactiveinflux.impl.query

import akka.http.scaladsl.model.Uri
import com.pygmalios.reactiveinflux.core.ReactiveinfluxRequest

abstract class BaseQuery(baseUri: Uri) extends ReactiveinfluxRequest {
  protected val queryUri = baseUri.withPath(BaseQuery.queryPath)
  protected def qUri(q: String) = queryUri.withQuery(Uri.Query("q" -> q))
}

object BaseQuery {
  val queryKeys = Set("q")
  val queryPath = Uri.Path("/query")
}
