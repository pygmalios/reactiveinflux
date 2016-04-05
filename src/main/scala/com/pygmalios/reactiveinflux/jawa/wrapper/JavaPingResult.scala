package com.pygmalios.reactiveinflux.jawa.wrapper

import com.pygmalios.reactiveinflux.jawa.PingResult
import com.pygmalios.{reactiveinflux => sc}

private[jawa] class JavaPingResult(underlying: sc.PingResult) extends PingResult {
  override def getInfluxDbVersion: String = underlying.influxDbVersion

  override def toString: String = underlying.toString
  override def hashCode(): Int = underlying.hashCode()
  override def equals(obj: scala.Any): Boolean = underlying.equals(obj)
}
