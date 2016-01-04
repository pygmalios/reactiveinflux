package com.pygmalios.reactiveinflux

import java.io.Closeable

import akka.actor.ActorSystem
import com.pygmalios.reactiveinflux.ReactiveInflux.{DbPassword, DbUsername, DbName}
import com.pygmalios.reactiveinflux.command.query.{Query, QueryParameters, QueryResult}
import com.pygmalios.reactiveinflux.command.write.WriteParameters
import com.pygmalios.reactiveinflux.impl.ActorSystemReactiveInflux
import com.pygmalios.reactiveinflux.model.PointNoTime
import com.pygmalios.reactiveinflux.result.PingResult
import com.typesafe.config.Config

import scala.concurrent.Future

/**
  * Reactive client for InfluxDB.
  */
trait ReactiveInflux extends Closeable {
  def ping(waitForLeaderSec: Option[Int] = None): Future[PingResult]
  def database(dbName: DbName, dbUsername: Option[DbUsername] = None, dbPassword: Option[DbPassword] = None): ReactiveInfluxDb
}

/**
  * Reactive API for InfluxDB database.
  */
trait ReactiveInfluxDb {
  def create(failIfExists: Boolean = false): Future[Unit]
  def drop(failIfNotExists: Boolean = false): Future[Unit]

  def write(point: PointNoTime): Future[Unit]
  def write(point: PointNoTime, params: WriteParameters): Future[Unit]
  def write(points: Iterable[PointNoTime], params: WriteParameters = WriteParameters()): Future[Unit]

  def query(q: Query): Future[QueryResult]
  def query(q: Query, params: QueryParameters): Future[QueryResult]
  def query(qs: Seq[Query], params: QueryParameters = QueryParameters()): Future[Seq[QueryResult]]
}

object ReactiveInflux {
  type DbName = String
  type DbUsername = String
  type DbPassword = String

  private val defaultClientName = "ReactiveInflux"
  private def defaultClientFactory(actorSystem: ActorSystem, config: ReactiveInfluxConfig): ReactiveInflux =
    ActorSystemReactiveInflux(actorSystem, config)

  /**
    * Create reactive Influx client. Normally there should be only one instance per application.
    *
    * @param name Provide a unique name if you plan to create more reactive Influx clients in a single JVM.
    * @param config Provide an overriding configuration.
    * @return Reactive Influx client.
    */
  def apply(name: String = defaultClientName,
            config: Option[Config] = None,
            clientFactory: (ActorSystem, ReactiveInfluxConfig) => ReactiveInflux = defaultClientFactory): ReactiveInflux = {
    val reactiveInfluxConfig = ReactiveInfluxConfig(config)
    val actorSystem = ActorSystem(name, reactiveInfluxConfig.reactiveinflux)
    clientFactory(actorSystem, reactiveInfluxConfig)
  }
}