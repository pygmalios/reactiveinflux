package com.pygmalios.reactiveinflux.command

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import com.pygmalios.reactiveinflux.error.{DatabaseNotFound, ReactiveInfluxError}
import com.pygmalios.reactiveinflux.impl.response.EmptyJsonResult

class DropDatabase(baseUri: Uri, name: String, failIfNotExists: Boolean) extends BaseQueryCommand(baseUri) {
  import DropDatabase._
  override type TResult = Unit
  override protected def responseFactory(httpResponse: HttpResponse) = new DropDatabaseResult(httpResponse, failIfNotExists)
  override val httpRequest = HttpRequest(uri = qUri(queryPattern.format(name)))
}

object DropDatabase {
  val queryPattern = "DROP DATABASE %s"
}

class DropDatabaseResult(httpResponse: HttpResponse, failIfNotExists: Boolean) extends EmptyJsonResult(httpResponse) {
  override protected def errorHandler: PartialFunction[ReactiveInfluxError, Option[ReactiveInfluxError]] = {
    case DatabaseNotFound(_) if !failIfNotExists => None
  }
}