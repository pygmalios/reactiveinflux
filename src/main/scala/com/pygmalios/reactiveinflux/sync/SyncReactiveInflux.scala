package com.pygmalios.reactiveinflux.sync

import java.io.Closeable

import com.pygmalios.reactiveinflux.ReactiveInfluxDbName
import com.pygmalios.reactiveinflux.command.PingResult
import com.pygmalios.reactiveinflux.command.query.{Query, QueryParameters, QueryResult}
import com.pygmalios.reactiveinflux.command.write.{PointNoTime, WriteParameters}
import com.pygmalios.reactiveinflux.sync.impl.WrappingSyncReactiveInflux
import com.pygmalios.reactiveinflux.{ReactiveInflux, ReactiveInfluxConfig}
import com.typesafe.config.Config

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Synchronous blocking client for InfluxDB.
  */
trait SyncReactiveInflux extends Closeable {
  def ping(waitForLeaderSec: Option[Int] = None)(implicit awaitAtMost: Duration): PingResult
  def database(implicit dbName: ReactiveInfluxDbName): SyncReactiveInfluxDb
  def config: ReactiveInfluxConfig
}

/**
  * Synchronous blocking API for InfluxDB database.
  */
trait SyncReactiveInfluxDb {
  def create()(implicit awaitAtMost: Duration): Unit
  def drop(failIfNotExists: Boolean = false)(implicit awaitAtMost: Duration): Unit

  def write(point: PointNoTime)(implicit awaitAtMost: Duration): Unit
  def write(point: PointNoTime, params: WriteParameters)(implicit awaitAtMost: Duration): Unit
  def write(points: Iterable[PointNoTime], params: WriteParameters = WriteParameters())(implicit awaitAtMost: Duration): Unit

  def query(q: Query)(implicit awaitAtMost: Duration): QueryResult
  def query(q: Query, params: QueryParameters)(implicit awaitAtMost: Duration): QueryResult
  def query(qs: Seq[Query], params: QueryParameters = QueryParameters())(implicit awaitAtMost: Duration): Seq[QueryResult]

  def config: ReactiveInfluxConfig
}

object SyncReactiveInflux {
  def await[T](f: => Future[T])(implicit awaitAtMost: Duration): T = Await.result(f, awaitAtMost)

  def apply(config: Option[Config] = None): SyncReactiveInflux =
    apply(config, ReactiveInflux.apply(ReactiveInfluxConfig(config)))

  def apply(config: Option[Config],
            reactiveInfluxFactory: => ReactiveInflux): SyncReactiveInflux =
    new WrappingSyncReactiveInflux(reactiveInfluxFactory)
}