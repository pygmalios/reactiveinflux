package com.pygmalios.reactiveinflux.command.query

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import com.pygmalios.reactiveinflux.ReactiveInflux.DbName
import com.pygmalios.reactiveinflux.error.{DatabaseNotFound, ReactiveInfluxError}
import com.pygmalios.reactiveinflux.response.EmptyJsonResponse

class DropDatabaseCommand(baseUri: Uri, dbName: DbName, failIfNotExists: Boolean) extends BaseQueryCommand(baseUri) {
  import DropDatabaseCommand._
  override type TResult = Unit
  override protected def responseFactory(httpResponse: HttpResponse, actorSystem: ActorSystem) =
    new DropDatabaseResponse(httpResponse, failIfNotExists, actorSystem)
  override val httpRequest = HttpRequest(uri = qUri(queryPattern.format(dbName)))
}

object DropDatabaseCommand {
  val queryPattern = "DROP DATABASE %s"
}

class DropDatabaseResponse(httpResponse: HttpResponse, failIfNotExists: Boolean, actorSystem: ActorSystem)
  extends EmptyJsonResponse(httpResponse, actorSystem) {
  override protected def errorHandler: PartialFunction[ReactiveInfluxError, Option[ReactiveInfluxError]] = {
    case DatabaseNotFound(_) if !failIfNotExists => None
  }
}