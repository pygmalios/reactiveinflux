package com.pygmalios.reactiveinflux.impl

import java.net.URI

import com.pygmalios.reactiveinflux.ReactiveInfluxConfig
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

private[reactiveinflux] class DefaultReactiveInfluxConfig(config: Option[Config] = None) extends ReactiveInfluxConfig {
  private val rootConfig = config match {
    case Some(c) => c.withFallback(ConfigFactory.load())
    case _ => ConfigFactory.load
  }

  override lazy val reactiveinflux = rootConfig.getConfig("reactiveinflux")
  override lazy val url = new URI(reactiveinflux.getString("url"))
  override lazy val username = withFallback(Try(reactiveinflux.getString("username")))
  override lazy val password = withFallback(Try(reactiveinflux.getString("password")))

  /** Attempts to acquire from environment, then java system properties. */
  private def withFallback[T](env: Try[T]): Option[T] = env match {
    case null  => None
    case value => value.toOption
  }
}
