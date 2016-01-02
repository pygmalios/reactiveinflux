package com.pygmalios.reactiveinflux.impl

import akka.actor.ActorSystem
import com.pygmalios.reactiveinflux.api.ReactiveInfluxClient
import com.typesafe.config.Config

private[reactiveinflux] class ActorSystemReactiveInfluxClient(name: String, config: Config) extends ReactiveInfluxClient {
  private val actorSystem = ActorSystem(name, config)

  override def close(): Unit = {
    actorSystem.terminate()
  }
}
