package com.pygmalios.reactiveinflux.impl

import akka.http.scaladsl.model.Uri
import com.pygmalios.reactiveinflux.ReactiveInfluxConfig
import com.typesafe.config.{Config, ConfigFactory}

private[reactiveinflux] class DefaultReactiveInfluxConfig(config: Option[Config] = None) extends ReactiveInfluxConfig {
  private val rootConfig = config match {
    case Some(c) => c.withFallback(ConfigFactory.load())
    case _ => ConfigFactory.load
  }

  val reactiveinflux = rootConfig.getConfig("reactiveinflux")
  val uri = Uri(reactiveinflux.getString("url"))
}
