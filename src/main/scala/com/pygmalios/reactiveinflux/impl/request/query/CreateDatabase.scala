package com.pygmalios.reactiveinflux.impl.request.query

import akka.http.scaladsl.model._
import com.pygmalios.reactiveinflux.api.result.errors.{DatabaseAlreadyExists, ReactiveinfluxError}
import com.pygmalios.reactiveinflux.impl.response.EmptyJsonResponse

class CreateDatabase(baseUri: Uri, name: String, failIfExists: Boolean) extends BaseQuery(baseUri) {
  import CreateDatabase._
  override type TResponse = Unit
  override protected def responseFactory(httpResponse: HttpResponse) = new CreateDatabaseResponse(httpResponse, failIfExists)
  override val httpRequest = HttpRequest(uri = qUri(queryPattern.format(name)))
}

object CreateDatabase {
  val queryPattern = "CREATE DATABASE %s"
}

class CreateDatabaseResponse(httpResponse: HttpResponse, failIfExists: Boolean) extends EmptyJsonResponse(httpResponse) {
  override protected def errorHandler: PartialFunction[ReactiveinfluxError, Option[ReactiveinfluxError]] = {
    case DatabaseAlreadyExists(_) if !failIfExists => None
  }
}