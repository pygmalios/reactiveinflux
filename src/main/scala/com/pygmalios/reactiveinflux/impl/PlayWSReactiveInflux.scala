package com.pygmalios.reactiveinflux.impl

import com.ning.http.client.AsyncHttpClientConfig
import com.pygmalios.reactiveinflux.ReactiveInflux._
import com.pygmalios.reactiveinflux._
import com.pygmalios.reactiveinflux.command.query._
import com.pygmalios.reactiveinflux.command.write.{PointNoTime, WriteCommand, WriteParameters}
import com.pygmalios.reactiveinflux.command.{PingCommand, PingResult}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.Future

class PlayWSReactiveInflux(val config: ReactiveInfluxConfig) extends ReactiveInflux with ReactiveInfluxCore
  with Logging {
  private lazy val ws = new NingWSClient(new AsyncHttpClientConfig.Builder().build())

  override def ping(waitForLeaderSec: Option[Int]): Future[PingResult] = execute(new PingCommand(config.uri))

  override def database(implicit params: ReactiveInfluxDbParams): ReactiveInfluxDb =
    new PlayWSReactiveInfluxDb(params.dbName, params.dbUsername, params.dbPassword, this)

  override def execute[R <: ReactiveInfluxCommand](command: R): Future[command.TResult] = {
    val httpRequest = command.httpRequest(ws)
    log.debug(s"${command.getClass.getSimpleName} HTTP ${httpRequest.method} ${httpRequest.url}")

    httpRequest.execute().map { httpResponse =>
      log.debug(s"Response: $httpResponse")
      command(httpRequest, httpResponse)
    }
  }

  override def close(): Unit = ws.close()
}

class PlayWSReactiveInfluxDb(dbName: DbName,
                             dbUsername: Option[DbUsername],
                             dbPassword: Option[DbPassword],
                             core: ReactiveInfluxCore) extends ReactiveInfluxDb {

  override def create(): Future[Unit] =
    core.execute(new CreateDatabaseCommand(core.config.uri, dbName))
  override def drop(failIfNotExists: Boolean): Future[Unit] =
    core.execute(new DropDatabaseCommand(core.config.uri, dbName, failIfNotExists))

  override def write(point: PointNoTime): Future[Unit] = write(point, WriteParameters())
  override def write(point: PointNoTime, params: WriteParameters): Future[Unit] = write(Seq(point), params)
  override def write(points: Iterable[PointNoTime], params: WriteParameters): Future[Unit] =
    core.execute(new WriteCommand(
      baseUri     = core.config.uri,
      dbName      = dbName,
      dbUsername  = dbUsername,
      dbPassword  = dbPassword,
      points      = points,
      params      = params
    ))

  override def query(q: Query): Future[QueryResult] = query(q, QueryParameters())
  override def query(q: Query, params: QueryParameters): Future[QueryResult] = query(Seq(q), params).map { results =>
    if (results.isEmpty)
      throw new ReactiveInfluxException(s"No results returned! [$q]")

    if (results.size > 1)
      throw new ReactiveInfluxException(s"Too many results returned! [${results.size}, $q]")

    results.head
  }
  override def query(qs: Seq[Query], params: QueryParameters): Future[Seq[QueryResult]] =
    core.execute(new QueryCommand(
      baseUri = core.config.uri,
      dbName  = dbName,
      qs      = qs,
      params  = params
    ))

  override def config = core.config
}

object PlayWSReactiveInflux {
  def apply(config: ReactiveInfluxConfig) =
    new PlayWSReactiveInflux(config)
}