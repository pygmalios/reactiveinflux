package com.pygmalios.reactiveinflux.jawa.wrapper.sync

import com.pygmalios.reactiveinflux.ReactiveInfluxDbName
import com.pygmalios.reactiveinflux.jawa.sync.{SyncReactiveInflux, SyncReactiveInfluxDb}
import com.pygmalios.reactiveinflux.jawa.{Conversions, PingResult, ReactiveInfluxConfig}
import com.pygmalios.{reactiveinflux => sc}

import scala.concurrent.duration.Duration

private[jawa] class JavaSyncReactiveInflux(underlying: sc.sync.SyncReactiveInflux)
                                          (implicit awaitAtMost: Duration) extends SyncReactiveInflux {
  override def ping(): PingResult = Conversions.toJava(underlying.ping())
  override def database(dbName: String): SyncReactiveInfluxDb = Conversions.toJava(underlying.database(ReactiveInfluxDbName(dbName)))
  override def getConfig: ReactiveInfluxConfig = Conversions.toJava(underlying.config)
  override def close(): Unit = underlying.close()

  override def toString: String = underlying.toString
  override def hashCode(): Int = underlying.hashCode()
  override def equals(obj: scala.Any): Boolean = underlying.equals(obj)
}
