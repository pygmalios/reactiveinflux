package com.pygmalios.reactiveinflux.sync.impl

import com.pygmalios.reactiveinflux.ReactiveInflux
import com.pygmalios.reactiveinflux.ReactiveInflux.ReactiveInfluxDbName
import com.pygmalios.reactiveinflux.sync.SyncReactiveInflux
import com.pygmalios.reactiveinflux.sync.SyncReactiveInflux._

import scala.concurrent.duration.Duration

class WrappingSyncReactiveInflux(reactiveInflux: ReactiveInflux) extends SyncReactiveInflux {
  override def ping(waitForLeaderSec: Option[Int])(implicit awaitAtMost: Duration) = await(reactiveInflux.ping(waitForLeaderSec))
  override def database(implicit dbName: ReactiveInfluxDbName) = new WrappingSyncReactiveInfluxDb(reactiveInflux.database(dbName))
  override def config = reactiveInflux.config
  override def close(): Unit = reactiveInflux.close()
}
