package com.pygmalios.reactiveinflux.command.query

import akka.http.scaladsl.model._
import com.pygmalios.reactiveinflux.ReactiveInflux.DbName
import com.pygmalios.reactiveinflux.response.EmptyJsonResponse

class CreateDatabaseCommand(baseUri: Uri, dbName: DbName) extends BaseQueryCommand(baseUri) {
  import CreateDatabaseCommand._
  override type TResult = Unit
  override protected def responseFactory(httpResponse: HttpResponse) = new CreateDatabaseResponse(httpResponse)
  override val httpRequest = HttpRequest(uri = qUri(queryPattern.format(dbName)))
}

object CreateDatabaseCommand {
  val queryPattern = "CREATE DATABASE %s"
}

private[reactiveinflux] class CreateDatabaseResponse(httpResponse: HttpResponse) extends EmptyJsonResponse(httpResponse)