package com.pygmalios.reactiveinflux.impl.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.pygmalios.reactiveinflux.api.ReactiveInfluxClient
import com.pygmalios.reactiveinflux.api.response.PingResponse
import com.pygmalios.reactiveinflux.core.{ReactiveinfluxCoreClient, ReactiveinfluxRequest}
import com.pygmalios.reactiveinflux.impl.api.response.SimplePingResponse
import com.pygmalios.reactiveinflux.impl.query.CreateDatabase
import com.pygmalios.reactiveinflux.impl.{Logging, ReactiveInfluxConfig}

import scala.concurrent.{ExecutionContext, Future}

private[reactiveinflux] class ActorSystemReactiveInfluxClient(actorSystem: ActorSystem, config: ReactiveInfluxConfig)
  extends ReactiveInfluxClient with ReactiveinfluxCoreClient with Logging {
  import ActorSystemReactiveInfluxClient._

  private implicit def system: ActorSystem = actorSystem
  private implicit def executionContext: ExecutionContext = actorSystem.dispatcher
  private implicit val materializer: ActorMaterializer = ActorMaterializer(Some(ActorMaterializerSettings(actorSystem)))
  private val http = Http(actorSystem)

  override def close(): Unit = {
    actorSystem.terminate()
  }

  override def ping(waitForLeaderSec: Option[Int]): Future[PingResponse] = {
    http.singleRequest(HttpRequest(uri = config.url + "ping")).map { httpResponse =>
      log.debug(s"Ping HTTP response. [$httpResponse]")
      SimplePingResponse(httpResponse.getHeader(pingVersionHeader).map(_.value()).getOrElse(""))
    }
  }

  override def createDatabase(name: String) = execute(new CreateDatabase(config.url, name))

  override def execute[R <: ReactiveinfluxRequest](request: R): Future[request.Response] = {
    http.singleRequest(request.httpRequest).map { httpResponse =>
      log.debug(s"HTTP response. [$httpResponse]")
      request(httpResponse)
    }
  }
}

private object ActorSystemReactiveInfluxClient {
  private val pingVersionHeader = "X-Influxdb-Version"
}