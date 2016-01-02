package com.pygmalios.reactiveinflux.impl.request.query

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import com.pygmalios.reactiveinflux.impl.response.EmptyJsonResponse

class DropDatabase(baseUri: Uri, name: String) extends BaseQuery(baseUri) {
  import DropDatabase._
  override type TResponse = Unit
  override protected def responseFactory(httpResponse: HttpResponse) = new EmptyJsonResponse(httpResponse)
  override val httpRequest = HttpRequest(uri = qUri(queryPattern.format(name)))
}

object DropDatabase {
  val queryPattern = "DROP DATABASE %s"
}
