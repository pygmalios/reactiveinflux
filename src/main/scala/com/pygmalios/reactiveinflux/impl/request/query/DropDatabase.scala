package com.pygmalios.reactiveinflux.impl.request.query

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import com.pygmalios.reactiveinflux.api.response.errors.{ReactiveinfluxError, DatabaseNotFound}
import com.pygmalios.reactiveinflux.impl.response.EmptyJsonResponse

class DropDatabase(baseUri: Uri, name: String, failIfNotExists: Boolean) extends BaseQuery(baseUri) {
  import DropDatabase._
  override type TResponse = Unit
  override protected def responseFactory(httpResponse: HttpResponse) = new DropDatabaseResponse(httpResponse, failIfNotExists)
  override val httpRequest = HttpRequest(uri = qUri(queryPattern.format(name)))
}

object DropDatabase {
  val queryPattern = "DROP DATABASE %s"
}

class DropDatabaseResponse(httpResponse: HttpResponse, failIfNotExists: Boolean) extends EmptyJsonResponse(httpResponse) {
  override protected def errorHandler: PartialFunction[ReactiveinfluxError, Option[ReactiveinfluxError]] = {
    case DatabaseNotFound(_) if !failIfNotExists => None
  }
}