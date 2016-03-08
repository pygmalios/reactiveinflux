package com.pygmalios.reactiveinflux

import java.net.URI

import com.pygmalios.reactiveinflux.impl.DefaultReactiveInfluxConfig
import com.typesafe.config.Config

trait ReactiveInfluxConfig extends Serializable {
  def reactiveinflux: Config
  def uri: URI
}

object ReactiveInfluxConfig {
  def apply(config: Option[Config] = None): ReactiveInfluxConfig = new DefaultReactiveInfluxConfig(config)
}