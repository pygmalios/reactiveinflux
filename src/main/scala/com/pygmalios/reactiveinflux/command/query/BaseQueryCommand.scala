package com.pygmalios.reactiveinflux.command.query

import akka.http.scaladsl.model.Uri
import com.pygmalios.reactiveinflux.ReactiveInfluxCommand

abstract class BaseQueryCommand(baseUri: Uri) extends ReactiveInfluxCommand {
  protected val queryUri = baseUri.withPath(BaseQueryCommand.queryPath)
  protected def qUri(q: String) = queryUri.withQuery(Uri.Query(otherParams + ("q" -> q)))
  protected def otherParams: Map[String, String] = Map.empty
}

object BaseQueryCommand {
  val queryKeys = Set("q")
  val queryPath = Uri.Path("/query")
}
