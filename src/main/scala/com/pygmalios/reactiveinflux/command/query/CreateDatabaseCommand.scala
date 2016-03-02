package com.pygmalios.reactiveinflux.command.query

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import com.pygmalios.reactiveinflux.ReactiveInflux.DbName
import com.pygmalios.reactiveinflux.response.EmptyJsonResponse

class CreateDatabaseCommand(baseUri: Uri, dbName: DbName) extends BaseQueryCommand(baseUri) {
  import CreateDatabaseCommand._
  override type TResult = Unit
  override protected def responseFactory(httpResponse: HttpResponse, actorSystem: ActorSystem) =
    new CreateDatabaseResponse(httpResponse, actorSystem)
  override val httpRequest = HttpRequest(uri = qUri(queryPattern.format(dbName)))
}

object CreateDatabaseCommand {
  val queryPattern = "CREATE DATABASE %s"
}

private[reactiveinflux] class CreateDatabaseResponse(httpResponse: HttpResponse, actorSystem: ActorSystem)
  extends EmptyJsonResponse(httpResponse, actorSystem)