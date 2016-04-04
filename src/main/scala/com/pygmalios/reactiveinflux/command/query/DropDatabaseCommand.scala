package com.pygmalios.reactiveinflux.command.query

import java.net.URI

import com.pygmalios.reactiveinflux.ReactiveInflux.ReactiveInfluxDbName
import com.pygmalios.reactiveinflux.error.{DatabaseNotFound, ReactiveInfluxError}
import com.pygmalios.reactiveinflux.response.EmptyJsonResponse
import play.api.libs.ws.{WSClient, WSResponse}

class DropDatabaseCommand(baseUri: URI, dbName: ReactiveInfluxDbName, failIfNotExists: Boolean)
  extends BaseQueryCommand(baseUri) {
  import DropDatabaseCommand._

  override type TResult = Unit
  override protected def responseFactory(wsResponse: WSResponse) =
    new DropDatabaseResponse(wsResponse, failIfNotExists)
  override def httpRequest(ws: WSClient) = ws.url(qUri(queryPattern.format(dbName.value)).toString)
}

object DropDatabaseCommand {
  val queryPattern = "DROP DATABASE %s"
}

class DropDatabaseResponse(wsResponse: WSResponse, failIfNotExists: Boolean)
  extends EmptyJsonResponse(wsResponse) {
  override protected def errorHandler: PartialFunction[ReactiveInfluxError, Option[ReactiveInfluxError]] = {
    case DatabaseNotFound(_) if !failIfNotExists => None
  }
}