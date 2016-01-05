package com.pygmalios.reactiveinflux.sync

import com.pygmalios.reactiveinflux.command.query.{Query, QueryParameters, QueryResult}
import com.pygmalios.reactiveinflux.command.write.{PointNoTime, WriteParameters}
import com.pygmalios.reactiveinflux.result.PingResult
import com.pygmalios.reactiveinflux.sync.ReactiveInfluxSyncClient._
import com.pygmalios.reactiveinflux.{ReactiveInflux, ReactiveInfluxDb}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Synchronous blocking client for InfluxDB.
  */
trait ReactiveInfluxSyncClient {
  def ping(waitForLeaderSec: Option[Int] = None): PingResult
  def database(name: String): ReactiveInfluxSyncDb
}

/**
  * Synchronous blocking API for InfluxDB database.
  */
trait ReactiveInfluxSyncDb {
  def create(failIfExists: Boolean = false): Unit
  def drop(failIfNotExists: Boolean = false): Unit

  def write(point: PointNoTime): Unit
  def write(point: PointNoTime, params: WriteParameters): Unit
  def write(points: Iterable[PointNoTime], params: WriteParameters = WriteParameters()): Unit

  def query(q: Query): QueryResult
  def query(q: Query, params: QueryParameters): QueryResult
  def query(qs: Seq[Query], params: QueryParameters = QueryParameters()): Seq[QueryResult]
}

object ReactiveInfluxSyncClient {
  val awaitAtMost = 30.seconds
  def await[T](f: => Future[T]): T = Await.result(f, awaitAtMost)

  def apply(reactiveInfluxClient: ReactiveInflux): ReactiveInfluxSyncClient =
    new WrappingReactiveInfluxSyncClient(reactiveInfluxClient)
}

private final class WrappingReactiveInfluxSyncClient(reactiveInflux: ReactiveInflux) extends ReactiveInfluxSyncClient {
  override def ping(waitForLeaderSec: Option[Int]) = await(reactiveInflux.ping(waitForLeaderSec))
  override def database(name: String) = new WrappingReactiveInfluxSyncDb(reactiveInflux.database(name))
}

private final class WrappingReactiveInfluxSyncDb(reactiveInfluxDb: ReactiveInfluxDb) extends ReactiveInfluxSyncDb {
  override def create(failIfExists: Boolean) = await(reactiveInfluxDb.create(failIfExists))
  override def drop(failIfNotExists: Boolean = false) = await(reactiveInfluxDb.drop(failIfNotExists))

  override def write(point: PointNoTime) = write(point, WriteParameters())
  override def write(point: PointNoTime, params: WriteParameters) = write(Seq(point), params)
  override def write(points: Iterable[PointNoTime], params: WriteParameters) = await(reactiveInfluxDb.write(points, params))

  override def query(q: Query) = query(q, QueryParameters())
  override def query(q: Query, params: QueryParameters) = await(reactiveInfluxDb.query(q, params))
  override def query(qs: Seq[Query], params: QueryParameters) = await(reactiveInfluxDb.query(qs, params))
}