package com.pygmalios.reactiveinflux.command

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import com.pygmalios.reactiveinflux.{ReactiveInfluxCommand, ReactiveInfluxResult}

class PingCommand(baseUri: Uri) extends ReactiveInfluxCommand {
  override type TResult = PingResult

  override protected def responseFactory(httpResponse: HttpResponse): ReactiveInfluxResult[PingResult] =
    SimplePingResult(httpResponse.getHeader(PingCommand.versionHeader).map(_.value()).getOrElse(""))

  override val httpRequest = HttpRequest(uri = baseUri.withPath(PingCommand.path))
}

object PingCommand {
  val path = Uri.Path("/ping")
  val versionHeader = "X-Influxdb-Version"
}

trait PingResult extends Serializable {
  def influxDbVersion: String
}

private[reactiveinflux] case class SimplePingResult(influxDbVersion: String) extends PingResult with ReactiveInfluxResult[PingResult] {
  override def result: PingResult = this
}