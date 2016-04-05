package com.pygmalios.reactiveinflux

import java.net.URI

import com.pygmalios.reactiveinflux.impl.DefaultReactiveInfluxConfig
import com.typesafe.config.Config

/**
  * ReactiveInflux configuration.
  */
trait ReactiveInfluxConfig extends Serializable {
  /**
    * InfluxDB URL.
    */
  def url: URI

  /**
    * User name.
    */
  def username: Option[String]

  /**
    * Password.
    */
  def password: Option[String]

  /**
    * Access to underlying Typesafe config.
    */
  def reactiveinflux: Config
}

object ReactiveInfluxConfig {
  def apply(config: Option[Config]): ReactiveInfluxConfig = new DefaultReactiveInfluxConfig(config)
  def apply(url: URI,
            username: Option[String] = None,
            password: Option[String] = None): ReactiveInfluxConfig = SimpleReactiveInfluxConfig(url, username, password)
}

private case class SimpleReactiveInfluxConfig(url: URI,
                                              username: Option[String],
                                              password: Option[String]) extends ReactiveInfluxConfig {
  override def reactiveinflux: Config = throw new UnsupportedOperationException()
}