package com.pygmalios.reactiveinflux

import java.io.Closeable

import com.pygmalios.reactiveinflux.command.PingResult
import com.pygmalios.reactiveinflux.command.query.QueryParameters
import com.pygmalios.reactiveinflux.command.write.WriteParameters
import com.pygmalios.reactiveinflux.impl.PlayWSReactiveInflux
import com.typesafe.config.Config

import scala.concurrent.Future

/**
  * Reactive client for InfluxDB.
  */
trait ReactiveInflux extends Closeable {
  def ping(waitForLeaderSec: Option[Int] = None): Future[PingResult]
  def database(implicit dbName: ReactiveInfluxDbName): ReactiveInfluxDb
  def config: ReactiveInfluxConfig
}

/**
  * Reactive API for InfluxDB database.
  */
trait ReactiveInfluxDb {
  def create(): Future[Unit]
  def drop(failIfNotExists: Boolean = false): Future[Unit]

  def write(point: PointNoTime): Future[Unit]
  def write(point: PointNoTime, params: WriteParameters): Future[Unit]
  def write(points: Iterable[PointNoTime], params: WriteParameters = WriteParameters()): Future[Unit]

  def query(q: Query): Future[QueryResult]
  def query(q: Query, params: QueryParameters): Future[QueryResult]
  def query(qs: Seq[Query], params: QueryParameters = QueryParameters()): Future[Seq[QueryResult]]

  def config: ReactiveInfluxConfig
}

object ReactiveInflux {
  /**
    * Create reactive Influx client. Normally there should be only one instance per application.
    */
  def apply(config: ReactiveInfluxConfig): ReactiveInflux = PlayWSReactiveInflux(config)

  /**
    * Create reactive Influx client from Typesafe config.
    */
  def apply(config: Option[Config] = None,
            clientFactory: (ReactiveInfluxConfig) => ReactiveInflux = apply): ReactiveInflux =
    clientFactory(ReactiveInfluxConfig(config))
}

/**
  * Typed database name.
  */
case class ReactiveInfluxDbName(value: String)