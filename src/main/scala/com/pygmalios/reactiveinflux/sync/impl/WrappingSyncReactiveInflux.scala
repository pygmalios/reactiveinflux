package com.pygmalios.reactiveinflux.sync.impl

import com.pygmalios.reactiveinflux.sync.SyncReactiveInflux
import com.pygmalios.reactiveinflux.{ReactiveInflux, ReactiveInfluxDbParams}

import scala.concurrent.duration.Duration
import SyncReactiveInflux._

class WrappingSyncReactiveInflux(reactiveInflux: ReactiveInflux) extends SyncReactiveInflux {
  override def ping(waitForLeaderSec: Option[Int])(implicit awaitAtMost: Duration) = await(reactiveInflux.ping(waitForLeaderSec))
  override def database(implicit params: ReactiveInfluxDbParams) = new WrappingSyncReactiveInfluxDb(reactiveInflux.database(params))
  override def config = reactiveInflux.config
  override def close(): Unit = reactiveInflux.close()
}
