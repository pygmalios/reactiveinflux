package com.pygmalios.reactiveinflux.jawa.sync

import java.util.concurrent.TimeUnit

import com.pygmalios.reactiveinflux.ReactiveInfluxDbName
import com.pygmalios.reactiveinflux.jawa.Conversions._
import com.pygmalios.reactiveinflux.jawa.{Conversions, PingResult, ReactiveInfluxConfig}
import com.pygmalios.{reactiveinflux => sc}

import scala.concurrent.duration.{Duration, FiniteDuration}

class JavaSyncReactiveInflux(val underlying: sc.sync.SyncReactiveInflux)
                            (implicit awaitAtMost: Duration) extends SyncReactiveInflux {
  def this(reactiveInfluxConfig: ReactiveInfluxConfig,
           awaitAtMostMillis: Long) = {
    this(sc.sync.SyncReactiveInflux(toScala(reactiveInfluxConfig)))(FiniteDuration.apply(awaitAtMostMillis, TimeUnit.MILLISECONDS))
  }

  override def ping(): PingResult = Conversions.toJava(underlying.ping())
  override def database(dbName: String): SyncReactiveInfluxDb = Conversions.toJava(underlying.database(ReactiveInfluxDbName(dbName)))
  override def getConfig: ReactiveInfluxConfig = Conversions.toJava(underlying.config)
  override def close(): Unit = underlying.close()

  override def toString: String = underlying.toString
  override def hashCode(): Int = underlying.hashCode()
  override def equals(obj: scala.Any): Boolean = underlying.equals(obj)
}
