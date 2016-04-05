package com.pygmalios.reactiveinflux.jawa

import com.pygmalios.reactiveinflux.jawa.sync.{SyncReactiveInflux, SyncReactiveInfluxDb}
import com.pygmalios.reactiveinflux.jawa.wrapper.sync.{JavaSyncReactiveInflux, JavaSyncReactiveInfluxDb}
import com.pygmalios.reactiveinflux.jawa.wrapper.{JavaPingResult, JavaReactiveInfluxConfig}
import com.pygmalios.{reactiveinflux => sc}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration

object Conversions {
  def toScala(reactiveInfluxConfig: ReactiveInfluxConfig): sc.ReactiveInfluxConfig = {
    sc.ReactiveInfluxConfig(
      reactiveInfluxConfig.getUrl,
      Option(reactiveInfluxConfig.getUsername),
      Option(reactiveInfluxConfig.getPassword))
  }

  def toJava(reactiveInflux: sc.sync.SyncReactiveInflux)(implicit awaitAtMost: Duration): SyncReactiveInflux =
    new JavaSyncReactiveInflux(reactiveInflux)(awaitAtMost)

  def toJava(reactiveInfluxConfig: sc.ReactiveInfluxConfig): ReactiveInfluxConfig =
    new JavaReactiveInfluxConfig(reactiveInfluxConfig)

  def toJava(syncReactiveInfluxDb: sc.sync.SyncReactiveInfluxDb)(implicit awaitAtMost: Duration): SyncReactiveInfluxDb =
    new JavaSyncReactiveInfluxDb(syncReactiveInfluxDb)

  def toJava(pingResult: sc.PingResult): PingResult =
    new JavaPingResult(pingResult)

  implicit def futureUnitToFutureVoid(promise: Future[Unit]): Future[Void] = promise.map(_ => null)
}
