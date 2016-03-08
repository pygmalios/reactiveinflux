package com.pygmalios.reactiveinflux.command.query

import java.net.URI

import com.pygmalios.reactiveinflux.ReactiveInfluxCommand

abstract class BaseQueryCommand(baseUri: URI) extends ReactiveInfluxCommand {
  protected val queryUri = new URI(baseUri.toString + BaseQueryCommand.queryPath)
  protected def qUri(q: String): URI = new URI(queryUri.toString + "?q=" + q)
  protected def otherParams: Map[String, String] = Map.empty
}

object BaseQueryCommand {
  val queryKeys = Set("q")
  val queryPath = "/query"
}
