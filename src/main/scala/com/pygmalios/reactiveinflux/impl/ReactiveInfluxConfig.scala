package com.pygmalios.reactiveinflux.impl

import java.net.URI

import com.typesafe.config.{Config, ConfigFactory}

private[impl] trait ReactiveInfluxConfig extends Serializable {
  def reactiveinflux: Config
  def url: URI
}

private class DefaultReactiveInfluxConfig(config: Option[Config] = None) extends ReactiveInfluxConfig {
  private val rootConfig = config match {
    case Some(c) => c.withFallback(ConfigFactory.load())
    case _ => ConfigFactory.load
  }

  val reactiveinflux = rootConfig.getConfig("reactiveinflux")
  val url = new URI(reactiveinflux.getString("url"))
}

private[reactiveinflux] object ReactiveInfluxConfig {
  def apply(config: Option[Config] = None): ReactiveInfluxConfig = new DefaultReactiveInfluxConfig(config)
}