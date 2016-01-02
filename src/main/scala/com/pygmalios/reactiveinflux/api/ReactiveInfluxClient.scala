package com.pygmalios.reactiveinflux.api

import java.io.Closeable

import com.pygmalios.reactiveinflux.impl.ActorSystemReactiveInfluxClient
import com.typesafe.config.{Config, ConfigFactory}

/**
  * Reactive client for InfluxDB.
  */
trait ReactiveInfluxClient extends Closeable {
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
    val rootConfig = config match {
      case Some(c) => c.withFallback(ConfigFactory.load)
      case _ => ConfigFactory.load
    }
    new ActorSystemReactiveInfluxClient(name, rootConfig)
  }
}