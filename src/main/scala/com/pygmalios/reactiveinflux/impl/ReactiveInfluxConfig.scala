package com.pygmalios.reactiveinflux.impl

import akka.http.scaladsl.model.Uri
import com.typesafe.config.{Config, ConfigFactory}

trait ReactiveInfluxConfig extends Serializable {
  def reactiveinflux: Config
  def url: Uri
}

class DefaultReactiveInfluxConfig(config: Option[Config] = None) extends ReactiveInfluxConfig {
  private val rootConfig = config match {
    case Some(c) => c.withFallback(ConfigFactory.load())
    case _ => ConfigFactory.load
  }

  val reactiveinflux = rootConfig.getConfig("reactiveinflux")
  val url = Uri(reactiveinflux.getString("url"))
}

object ReactiveInfluxConfig {
  def apply(config: Option[Config] = None): ReactiveInfluxConfig = new DefaultReactiveInfluxConfig(config)
}