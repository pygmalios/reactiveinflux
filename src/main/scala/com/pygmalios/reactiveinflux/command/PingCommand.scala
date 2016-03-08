package com.pygmalios.reactiveinflux.command

import java.net.URI

import com.pygmalios.reactiveinflux.{ReactiveInfluxCommand, ReactiveInfluxResult}
import play.api.libs.ws._

class PingCommand(baseUri: URI) extends ReactiveInfluxCommand {
  override type TResult = PingResult

  override protected def responseFactory(httpResponse: WSResponse): ReactiveInfluxResult[PingResult] =
    SimplePingResult(httpResponse.header(PingCommand.versionHeader).getOrElse(""))

  override def httpRequest(ws: WSClient) = ws.url(baseUri.toString + PingCommand.path)
}

object PingCommand {
  val path = "/ping"
  val versionHeader = "X-Influxdb-Version"
}

trait PingResult extends Serializable {
  def influxDbVersion: String
}

private[reactiveinflux] case class SimplePingResult(influxDbVersion: String) extends PingResult with ReactiveInfluxResult[PingResult] {
  override def result: PingResult = this
}