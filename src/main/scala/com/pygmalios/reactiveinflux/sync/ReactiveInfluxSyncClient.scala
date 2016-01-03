package com.pygmalios.reactiveinflux.sync

import com.pygmalios.reactiveinflux.model.PointNoTime
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
  def write(points: Iterable[PointNoTime]): Unit
}

object ReactiveInfluxSyncClient {
  val awaitAtMost = 30.seconds
  def await[T](f: => Future[T]): T = Await.result(f, awaitAtMost)

  def apply(reactiveInfluxClient: ReactiveInflux): ReactiveInfluxSyncClient =
    new WrappingReactiveInfluxSyncClient(reactiveInfluxClient)
}

private final class WrappingReactiveInfluxSyncClient(reactiveInfluxClient: ReactiveInflux) extends ReactiveInfluxSyncClient {
  def ping(waitForLeaderSec: Option[Int]) = await(reactiveInfluxClient.ping(waitForLeaderSec))
  def database(name: String) = new WrappingReactiveInfluxSyncDb(reactiveInfluxClient.database(name))
}

private final class WrappingReactiveInfluxSyncDb(reactiveInfluxDb: ReactiveInfluxDb) extends ReactiveInfluxSyncDb {
  def create(failIfExists: Boolean) = await(reactiveInfluxDb.create(failIfExists))
  def drop(failIfNotExists: Boolean = false) = await(reactiveInfluxDb.drop(failIfNotExists))
  def write(point: PointNoTime) = await(reactiveInfluxDb.write(point))
  def write(points: Iterable[PointNoTime]) = await(reactiveInfluxDb.write(points))
}