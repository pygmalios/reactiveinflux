package com.pygmalios.reactiveinflux.command

import java.net.URI

import com.pygmalios.reactiveinflux.ReactiveInfluxCommand
import com.pygmalios.reactiveinflux.uri._
import play.api.libs.ws.{WSClient, WSRequestHolder}

abstract class BaseCommand(rootUri: URI,
                           commandPath: URIPath) extends ReactiveInfluxCommand {
  /**
    * Command-specific query string
    */
  protected def queryString: URIQueryString = URIQueryString.empty

  override def httpRequest(ws: WSClient): WSRequestHolder = {
    val uri = rootUri
      .withPath(commandPath)
      .withQuery(queryString)

    ws.url(uri.toString)
  }
}

object BaseCommand {
  val usernameQ = "u"
  val passwordQ = "p"
}