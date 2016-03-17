package com.pygmalios.reactiveinflux

import java.net.URI

import com.pygmalios.reactiveinflux.impl.DefaultReactiveInfluxConfig
import com.typesafe.config.Config

trait ReactiveInfluxConfig extends Serializable {
  def reactiveinflux: Config
  def url: URI
  def db: Option[String]
  def username: Option[String]
  def password: Option[String]
}

object ReactiveInfluxConfig {
  def apply(config: Option[Config] = None): ReactiveInfluxConfig = new DefaultReactiveInfluxConfig(config)
}