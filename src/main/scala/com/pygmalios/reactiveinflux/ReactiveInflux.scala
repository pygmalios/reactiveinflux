package com.pygmalios.reactiveinflux

import java.io.Closeable

import akka.actor.ActorSystem
import com.pygmalios.reactiveinflux.impl.ActorSystemReactiveInflux
import com.pygmalios.reactiveinflux.model.PointNoTime
import com.pygmalios.reactiveinflux.result.PingResult
import com.typesafe.config.Config

import scala.concurrent.Future

/**
  * Reactive client for InfluxDB.
  */
trait ReactiveInflux extends Closeable {
  def ping(waitForLeaderSec: Option[Int] = None): Future[PingResult]
  def database(name: String): ReactiveInfluxDb
}

/**
  * Reactive API for InfluxDB database.
  */
trait ReactiveInfluxDb {
  def create(failIfExists: Boolean = false): Future[Unit]
  def drop(failIfNotExists: Boolean = false): Future[Unit]
  def write(point: PointNoTime): Future[Unit]
  def write(points: Iterable[PointNoTime]): Future[Unit]
}

object ReactiveInflux {
  private val defaultClientName = "ReactiveInfluxClient"
  private def defaultClientFactory(actorSystem: ActorSystem, config: ReactiveInfluxConfig): ReactiveInflux =
    ActorSystemReactiveInflux(actorSystem, config)

  /**
    * Create reactive Influx client. Normally there should be only one instance per application.
    *
    * @param name Provide a unique name if you plan to create more reactive Influx clients in a single JVM.
    * @param config Provide an overriding configuration.
    * @return Reactive Influx client.
    */
  def apply(name: String = defaultClientName,
            config: Option[Config] = None,
            clientFactory: (ActorSystem, ReactiveInfluxConfig) => ReactiveInflux = defaultClientFactory): ReactiveInflux = {
    val reactiveInfluxConfig = ReactiveInfluxConfig(config)
    val actorSystem = ActorSystem(name, reactiveInfluxConfig.reactiveinflux)
    clientFactory(actorSystem, reactiveInfluxConfig)
  }
}