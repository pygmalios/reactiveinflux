package com.pygmalios.reactiveinflux.command.query

import java.net.URI

import com.pygmalios.reactiveinflux.ReactiveInfluxCommand
import com.pygmalios.reactiveinflux.impl.URIUtils

abstract class BaseQueryCommand(baseUri: URI) extends ReactiveInfluxCommand {
  protected val queryUri = URIUtils.appendPath(baseUri, BaseQueryCommand.queryPath)
  protected def qUri(q: String): URI =
    new URI(queryUri.toString + URIUtils.queryToString(Map(BaseQueryCommand.queryKey -> q)))
  protected def otherParams: Map[String, String] = Map.empty
}

object BaseQueryCommand {
  val queryKey = "q"
  val queryPath = "/query"
}
