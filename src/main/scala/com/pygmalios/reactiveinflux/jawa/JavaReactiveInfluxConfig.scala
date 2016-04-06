package com.pygmalios.reactiveinflux.jawa

import java.net.URI

import com.pygmalios.{reactiveinflux => sc}
import com.typesafe.config.Config

class JavaReactiveInfluxConfig(val underlying: sc.ReactiveInfluxConfig) extends ReactiveInfluxConfig {
  def this(url: URI, username: String, password: String) {
    this(sc.ReactiveInfluxConfig(url, Option(username), Option(password)))
  }

  def this(url: URI) {
    this(url, null, null)
  }

  override def getUrl: URI = underlying.url
  override def getUsername: String = underlying.username.orNull
  override def getPassword: String = underlying.password.orNull
  override def getConfig: Config = underlying.reactiveinflux

  override def toString: String = underlying.toString
  override def hashCode(): Int = underlying.hashCode()
  override def equals(obj: scala.Any): Boolean = underlying.equals(obj)
}
