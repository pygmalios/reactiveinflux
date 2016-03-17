package com.pygmalios.reactiveinflux.command.query

import java.net.URI

import com.pygmalios.reactiveinflux.command.BaseCommand
import com.pygmalios.reactiveinflux.impl.URIUtils
import com.pygmalios.reactiveinflux.uri.URIPath

abstract class BaseQueryCommand(baseUri: URI) extends BaseCommand(baseUri, BaseQueryCommand.queryPath) {
  protected val queryUri = URIUtils.appendPath(baseUri, BaseQueryCommand.queryPath.toString)
  protected def qUri(q: String): URI = URIUtils.appendQuery(
    queryUri,
    (otherParams + (BaseQueryCommand.queryKey -> q)).toVector:_*)
  protected def otherParams: Map[String, String] = Map.empty
}

object BaseQueryCommand {
  val queryKey = "q"
  val queryPath = URIPath("query")
}
