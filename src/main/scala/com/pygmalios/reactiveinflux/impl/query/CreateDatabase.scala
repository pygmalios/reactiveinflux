package com.pygmalios.reactiveinflux.impl.query

import akka.http.scaladsl.model._
import com.pygmalios.reactiveinflux.impl.response.EmptyJsonResponse
import org.slf4j.LoggerFactory

class CreateDatabase(baseUri: Uri, name: String) extends BaseQuery(baseUri) {
  import CreateDatabase._
  override type Response = Unit
  override val httpRequest = HttpRequest(uri = qUri(queryPattern.format(name)))
  override protected val responseFactory = new EmptyJsonResponse(_)
}

private object CreateDatabase {
  val log = LoggerFactory.getLogger(classOf[CreateDatabase])
  val queryPattern = "CREATE DATABASE %s"
}