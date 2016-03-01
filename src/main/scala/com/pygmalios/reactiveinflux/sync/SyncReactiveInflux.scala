package com.pygmalios.reactiveinflux.sync

import akka.actor.ActorSystem
import com.pygmalios.reactiveinflux.command.PingResult
import com.pygmalios.reactiveinflux.command.query.{Query, QueryParameters, QueryResult}
import com.pygmalios.reactiveinflux.command.write.{PointNoTime, WriteParameters}
import com.pygmalios.reactiveinflux.sync.SyncReactiveInflux._
import com.pygmalios.reactiveinflux.{ReactiveInfluxConfig, ReactiveInfluxDbParams, ReactiveInflux, ReactiveInfluxDb}
import com.typesafe.config.Config

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Synchronous blocking client for InfluxDB.
  */
trait SyncReactiveInflux {
  def ping(waitForLeaderSec: Option[Int] = None)(implicit awaitAtMost: Duration): PingResult
  def database(implicit params: ReactiveInfluxDbParams): SyncReactiveInfluxDb
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
}

object SyncReactiveInflux {
  def await[T](f: => Future[T])(implicit awaitAtMost: Duration): T = Await.result(f, awaitAtMost)

  def apply(name: String = ReactiveInflux.defaultClientName,
            config: Option[Config] = None,
            clientFactory: (ActorSystem, ReactiveInfluxConfig) => ReactiveInflux = ReactiveInflux.defaultClientFactory): SyncReactiveInflux =
    new WrappingSyncReactiveInflux(ReactiveInflux(name, config, clientFactory))
}

private final class WrappingSyncReactiveInflux(reactiveInflux: ReactiveInflux) extends SyncReactiveInflux {
  override def ping(waitForLeaderSec: Option[Int])(implicit awaitAtMost: Duration) = await(reactiveInflux.ping(waitForLeaderSec))
  override def database(implicit params: ReactiveInfluxDbParams) = new WrappingSyncReactiveInfluxDb(reactiveInflux.database(params))
  override def config = reactiveInflux.config
}

private final class WrappingSyncReactiveInfluxDb(reactiveInfluxDb: ReactiveInfluxDb) extends SyncReactiveInfluxDb {
  override def create()(implicit awaitAtMost: Duration) = await(reactiveInfluxDb.create())
  override def drop(failIfNotExists: Boolean = false)(implicit awaitAtMost: Duration) = await(reactiveInfluxDb.drop(failIfNotExists))

  override def write(point: PointNoTime)(implicit awaitAtMost: Duration) = write(point, WriteParameters())
  override def write(point: PointNoTime, params: WriteParameters)(implicit awaitAtMost: Duration) = write(Seq(point), params)
  override def write(points: Iterable[PointNoTime], params: WriteParameters)(implicit awaitAtMost: Duration) = await(reactiveInfluxDb.write(points, params))

  override def query(q: Query)(implicit awaitAtMost: Duration) = query(q, QueryParameters())
  override def query(q: Query, params: QueryParameters)(implicit awaitAtMost: Duration) = await(reactiveInfluxDb.query(q, params))
  override def query(qs: Seq[Query], params: QueryParameters)(implicit awaitAtMost: Duration) = await(reactiveInfluxDb.query(qs, params))
}