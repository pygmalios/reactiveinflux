package com.pygmalios.reactiveinflux.impl.request.query

import akka.http.scaladsl.model._
import com.pygmalios.reactiveinflux.impl.response.EmptyJsonResponse

class CreateDatabase(baseUri: Uri, name: String) extends BaseQuery(baseUri) {
  import CreateDatabase._
  override type TResponse = Unit
  override protected def responseFactory(httpResponse: HttpResponse) = new EmptyJsonResponse(httpResponse)
  override val httpRequest = HttpRequest(uri = qUri(queryPattern.format(name)))
}

object CreateDatabase {
  val queryPattern = "CREATE DATABASE %s"
}