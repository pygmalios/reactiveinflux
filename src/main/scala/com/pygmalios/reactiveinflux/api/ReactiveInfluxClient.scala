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
  /**
    * Creates GET `/ping` request.
    *
    * @param waitForLeaderSec Number of seconds to wait before returning a response.
    * @return Request response.
    */
  def ping(waitForLeaderSec: Option[Int] = None): Future[PingResponse]
}

object ReactiveInfluxClient {
  private val defaultClientName = "ReactiveInfluxClient"

  /**
    * Create reactive Influx client. Normally there should be only one instance per application.
    *
    * @param name Provide a unique name if you plan to create more reactive Influx clients in a single JVM.
    * @param config Provide an overriding configuration.
    * @return Reactive Influx client.
    */
  def apply(name: String = defaultClientName,
            config: Option[Config] = None): ReactiveInfluxClient = {
    val reactiveInfluxConfig = ReactiveInfluxConfig(config)
    val actorSystem = ActorSystem(name, reactiveInfluxConfig.reactiveinflux)
    new ActorSystemReactiveInfluxClient(actorSystem, reactiveInfluxConfig)
  }
}