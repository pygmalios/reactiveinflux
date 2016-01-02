package com.pygmalios.reactiveinflux.impl.request

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import com.pygmalios.reactiveinflux.api.response.PingResponse
import com.pygmalios.reactiveinflux.core.{ReactiveinfluxRequest, ReactiveinfluxResponse}

class Ping(baseUri: Uri) extends ReactiveinfluxRequest {
  override type TResponse = PingResponse

  override protected def responseFactory(httpResponse: HttpResponse): ReactiveinfluxResponse[PingResponse] =
    SimplePingResponse(httpResponse.getHeader(Ping.versionHeader).map(_.value()).getOrElse(""))

  override val httpRequest = HttpRequest(uri = baseUri.withPath(Ping.path))
}

case class SimplePingResponse(influxDbVersion: String) extends PingResponse with ReactiveinfluxResponse[PingResponse] {
  override def result: PingResponse = this
}

object Ping {
  val path = Uri.Path("/ping")
  val versionHeader = "X-Influxdb-Version"
}