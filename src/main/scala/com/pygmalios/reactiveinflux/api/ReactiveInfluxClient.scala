package com.pygmalios.reactiveinflux.api

import java.io.Closeable

import akka.actor.ActorSystem
import com.pygmalios.reactiveinflux.api.response.PingResponse
import com.pygmalios.reactiveinflux.impl.ReactiveInfluxConfig
import com.pygmalios.reactiveinflux.impl.api.ActorSystemReactiveInfluxClient
import com.typesafe.config.Config

import scala.concurrent.Future

/**
  * Reactive client for InfluxDB.
  */
trait ReactiveInfluxClient extends Closeable {
  def ping(waitForLeaderSec: Option[Int] = None): Future[PingResponse]
  def createDatabase(name: String): Future[Unit]
  def dropDatabase(name: String): Future[Unit]
}

object ReactiveInfluxClient {
  private val defaultClientName = "ReactiveInfluxClient"
  private def defaultClientFactory(actorSystem: ActorSystem, config: ReactiveInfluxConfig) =
    new ActorSystemReactiveInfluxClient(actorSystem, config)

  /**
    * Create reactive Influx client. Normally there should be only one instance per application.
    *
    * @param name Provide a unique name if you plan to create more reactive Influx clients in a single JVM.
    * @param config Provide an overriding configuration.
    * @return Reactive Influx client.
    */
  def apply(name: String = defaultClientName,
            config: Option[Config] = None,
            clientFactory: (ActorSystem, ReactiveInfluxConfig) => ReactiveInfluxClient = defaultClientFactory): ReactiveInfluxClient = {
    val reactiveInfluxConfig = ReactiveInfluxConfig(config)
    val actorSystem = ActorSystem(name, reactiveInfluxConfig.reactiveinflux)
    clientFactory(actorSystem, reactiveInfluxConfig)
  }
}