package com.pygmalios.reactiveinflux.command.query

import java.net.URI

import com.pygmalios.reactiveinflux.ReactiveInflux.ReactiveInfluxDbName
import com.pygmalios.reactiveinflux.response.EmptyJsonResponse
import play.api.libs.ws.{WSClient, WSResponse}

class CreateDatabaseCommand(baseUri: URI, dbName: ReactiveInfluxDbName) extends BaseQueryCommand(baseUri) {
  import CreateDatabaseCommand._
  override type TResult = Unit
  override protected def responseFactory(wsResponse: WSResponse) = new CreateDatabaseResponse(wsResponse)
  override def httpRequest(ws: WSClient) = ws.url(qUri(queryPattern.format(dbName.value)).toString)

}

object CreateDatabaseCommand {
  val queryPattern = "CREATE DATABASE %s"
}

private[reactiveinflux] class CreateDatabaseResponse(wsResponse: WSResponse) extends EmptyJsonResponse(wsResponse)