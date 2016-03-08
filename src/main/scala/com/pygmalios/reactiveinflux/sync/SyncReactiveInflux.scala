package com.pygmalios.reactiveinflux.sync

import java.io.Closeable

import akka.actor.ActorSystem
import com.pygmalios.reactiveinflux.command.PingResult
import com.pygmalios.reactiveinflux.command.query.{Query, QueryParameters, QueryResult}
import com.pygmalios.reactiveinflux.command.write.{PointNoTime, WriteParameters}
import com.pygmalios.reactiveinflux.sync.impl.WrappingSyncReactiveInflux
import com.pygmalios.reactiveinflux.{ReactiveInflux, ReactiveInfluxConfig, ReactiveInfluxDbParams}
import com.typesafe.config.Config

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Synchronous blocking client for InfluxDB.
  */
trait SyncReactiveInflux extends Closeable {
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

  def config: ReactiveInfluxConfig
}

object SyncReactiveInflux {
  def await[T](f: => Future[T])(implicit awaitAtMost: Duration): T = Await.result(f, awaitAtMost)

  def apply(actorSystem: ActorSystem,
            name: String = ReactiveInflux.defaultClientName,
            config: Option[Config] = None): SyncReactiveInflux =
    apply(ReactiveInflux.defaultClientFactory(actorSystem, ReactiveInfluxConfig(config)), name, config)

  def apply(name: String,
            config: Option[Config],
            clientFactory: (ActorSystem, ReactiveInfluxConfig) => ReactiveInflux): SyncReactiveInflux =
    apply(ReactiveInflux(name, config, clientFactory), name, config)


  def apply(reactiveInfluxFactory: => ReactiveInflux,
            name: String,
            config: Option[Config]): SyncReactiveInflux =
    new WrappingSyncReactiveInflux(reactiveInfluxFactory)
}