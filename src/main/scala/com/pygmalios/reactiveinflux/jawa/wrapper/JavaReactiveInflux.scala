package com.pygmalios.reactiveinflux.jawa.wrapper

import com.pygmalios.reactiveinflux.jawa._
import com.pygmalios.{reactiveinflux => sc}
import play.libs.F.Promise

import scala.concurrent.ExecutionContext.Implicits.global

private[jawa] class JavaReactiveInflux(underlying: sc.ReactiveInflux) extends ReactiveInflux {
  override def ping(): Promise[PingResult] = Promise.wrap(underlying.ping().map(Conversions.toJava))
  override def database(dbName: String): ReactiveInfluxDb = ??? //TODO: underlying.database(new ReactiveInfluxDbName(dbName))
  override def getConfig: ReactiveInfluxConfig = ??? // TODO:
  override def close() = underlying.close()

  override def toString: String = underlying.toString
  override def hashCode(): Int = underlying.hashCode()
  override def equals(obj: scala.Any): Boolean = underlying.equals(obj)
}
