package com.pygmalios.reactiveinflux.sync.impl

import com.pygmalios.reactiveinflux.ReactiveInfluxDb
import com.pygmalios.reactiveinflux.command.query.{Query, QueryParameters}
import com.pygmalios.reactiveinflux.command.write.{PointNoTime, WriteParameters}
import com.pygmalios.reactiveinflux.sync.{SyncReactiveInflux, SyncReactiveInfluxDb}

import scala.concurrent.duration.Duration
import SyncReactiveInflux._

class WrappingSyncReactiveInfluxDb(reactiveInfluxDb: ReactiveInfluxDb) extends SyncReactiveInfluxDb {
  override def create()(implicit awaitAtMost: Duration) = await(reactiveInfluxDb.create())
  override def drop(failIfNotExists: Boolean = false)(implicit awaitAtMost: Duration) = await(reactiveInfluxDb.drop(failIfNotExists))

  override def write(point: PointNoTime)(implicit awaitAtMost: Duration) = write(point, WriteParameters())
  override def write(point: PointNoTime, params: WriteParameters)(implicit awaitAtMost: Duration) = write(Seq(point), params)
  override def write(points: Iterable[PointNoTime], params: WriteParameters)(implicit awaitAtMost: Duration) = await(reactiveInfluxDb.write(points, params))

  override def query(q: Query)(implicit awaitAtMost: Duration) = query(q, QueryParameters())
  override def query(q: Query, params: QueryParameters)(implicit awaitAtMost: Duration) = await(reactiveInfluxDb.query(q, params))
  override def query(qs: Seq[Query], params: QueryParameters)(implicit awaitAtMost: Duration) = await(reactiveInfluxDb.query(qs, params))
}
