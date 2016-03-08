package com.pygmalios.reactiveinflux

import java.io.Closeable

import com.pygmalios.reactiveinflux.ReactiveInflux.{DbName, DbPassword, DbUsername}
import com.pygmalios.reactiveinflux.command.PingResult
import com.pygmalios.reactiveinflux.command.query.{Query, QueryParameters, QueryResult}
import com.pygmalios.reactiveinflux.command.write.{PointNoTime, WriteParameters}
import com.pygmalios.reactiveinflux.impl.PlayWSReactiveInflux
import com.typesafe.config.Config

import scala.concurrent.Future

/**
  * Reactive client for InfluxDB.
  */
trait ReactiveInflux extends Closeable {
  def ping(waitForLeaderSec: Option[Int] = None): Future[PingResult]
  def database(implicit params: ReactiveInfluxDbParams): ReactiveInfluxDb
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

trait ReactiveInfluxDbParams {
  def dbName: DbName
  def dbUsername: Option[DbUsername]
  def dbPassword: Option[DbPassword]
}

object ReactiveInfluxDbParams {
  def apply(dbName: DbName,
            dbUsername: Option[DbUsername] = None,
            dbPassword: Option[DbPassword] = None): ReactiveInfluxDbParams =
    SimpleReactiveInfluxDbParams(dbName, dbUsername, dbPassword)
}

private case class SimpleReactiveInfluxDbParams(dbName: DbName,
                                                dbUsername: Option[DbUsername],
                                                dbPassword: Option[DbPassword])
  extends ReactiveInfluxDbParams

object ReactiveInflux {
  type DbName = String
  type DbUsername = String
  type DbPassword = String

  val defaultClientName = "ReactiveInflux"
  def defaultClientFactory(config: ReactiveInfluxConfig): ReactiveInflux =
    PlayWSReactiveInflux(config)

  /**
    * Create reactive Influx client. Normally there should be only one instance per application.
    */
  def apply(name: String = defaultClientName,
            config: Option[Config] = None,
            clientFactory: (ReactiveInfluxConfig) => ReactiveInflux = defaultClientFactory): ReactiveInflux =
    clientFactory(ReactiveInfluxConfig(config))
}