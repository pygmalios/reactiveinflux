package com.pygmalios.reactiveinflux.jawa.wrapper

import java.net.URI

import com.pygmalios.reactiveinflux.jawa.ReactiveInfluxConfig
import com.typesafe.config.Config
import com.pygmalios.{reactiveinflux => sc}

private[jawa] class JavaReactiveInfluxConfig(underlying: sc.ReactiveInfluxConfig) extends ReactiveInfluxConfig {
  override def getUrl: URI = underlying.url
  override def getUsername: String = underlying.username.orNull
  override def getPassword: String = underlying.password.orNull
  override def getConfig: Config = underlying.reactiveinflux

  override def toString: String = underlying.toString
  override def hashCode(): Int = underlying.hashCode()
  override def equals(obj: scala.Any): Boolean = underlying.equals(obj)
}
