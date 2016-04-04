package com.pygmalios.reactiveinflux.impl

import com.ning.http.client.AsyncHttpClientConfig
import com.pygmalios.reactiveinflux.ReactiveInflux._
import com.pygmalios.reactiveinflux._
import com.pygmalios.reactiveinflux.command.query._
import com.pygmalios.reactiveinflux.command.write.{PointNoTime, WriteCommand, WriteParameters}
import com.pygmalios.reactiveinflux.command.{PingCommand, PingResult}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.ning.{NingWSClient, NingWSResponse}
import play.api.libs.ws.{WSAuthScheme, WSRequestHolder}

import scala.concurrent.Future

class PlayWSReactiveInflux(val config: ReactiveInfluxConfig) extends ReactiveInflux with ReactiveInfluxCore
  with Logging {
  private lazy val ws = new NingWSClient(new AsyncHttpClientConfig.Builder().build())

  override def ping(waitForLeaderSec: Option[Int]): Future[PingResult] = execute(new PingCommand(config.url))

  override def database(implicit dbName: ReactiveInfluxDbName): ReactiveInfluxDb =
    new PlayWSReactiveInfluxDb(dbName, this)

  override def execute[R <: ReactiveInfluxCommand](command: R): Future[command.TResult] = {
    val httpRequest = authHttpRequest(command)
    log.info(s"${command.getClass.getSimpleName} ${command.logInfo} HTTP ${httpRequest.method} ${httpRequest.url}")

    httpRequest.execute().map {
      case r: NingWSResponse =>
        val headers = r.allHeaders.map {
          case (k, v) => s"$k=${v.mkString(",")}"
        }
        log.info(s"Response: ${r.status} Length=${r.body.size} ${headers.mkString(";")}")
        r
      case other => other
    }.map(command(httpRequest, _))
  }

  override def close(): Unit = ws.close()

  private def authHttpRequest[R <: ReactiveInfluxCommand](command: R): WSRequestHolder = {
    val httpRequest = command.httpRequest(ws)
    if (config.username.nonEmpty && config.password.nonEmpty) {
      httpRequest.withAuth(config.username.get, config.password.get, WSAuthScheme.BASIC)
    }
    else {
      httpRequest
    }
  }
}

class PlayWSReactiveInfluxDb(dbName: ReactiveInfluxDbName,
                             core: ReactiveInfluxCore) extends ReactiveInfluxDb {

  override def create(): Future[Unit] =
    core.execute(new CreateDatabaseCommand(core.config.url, dbName))
  override def drop(failIfNotExists: Boolean): Future[Unit] =
    core.execute(new DropDatabaseCommand(core.config.url, dbName, failIfNotExists))

  override def write(point: PointNoTime): Future[Unit] = write(point, WriteParameters())
  override def write(point: PointNoTime, params: WriteParameters): Future[Unit] = write(Seq(point), params)
  override def write(points: Iterable[PointNoTime], params: WriteParameters): Future[Unit] =
    core.execute(new WriteCommand(
      baseUri     = core.config.url,
      dbName      = dbName,
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
      baseUri = core.config.url,
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