package com.pygmalios.reactiveinflux.impl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.pygmalios.reactiveinflux._
import com.pygmalios.reactiveinflux.command.query.{Query, QueryParameters, QueryResult}
import com.pygmalios.reactiveinflux.command.write.{WriteCommand, WriteParameters}
import com.pygmalios.reactiveinflux.command.{CreateDatabaseCommand, DropDatabaseCommand, PingCommand}
import com.pygmalios.reactiveinflux.model.PointNoTime

import scala.concurrent.{ExecutionContext, Future}

class ActorSystemReactiveInflux(actorSystem: ActorSystem, val config: ReactiveInfluxConfig)
  extends ReactiveInflux with ReactiveInfluxCore with Logging {

  protected implicit def system: ActorSystem = actorSystem
  implicit def executionContext: ExecutionContext = actorSystem.dispatcher
  protected implicit val materializer: ActorMaterializer = ActorMaterializer(Some(ActorMaterializerSettings(actorSystem)))
  protected val http = Http(actorSystem)

  override def close(): Unit = {
    actorSystem.terminate()
  }

  override def ping(waitForLeaderSec: Option[Int]) = execute(new PingCommand(config.uri))

  override def database(name: String, username: Option[String], password: Option[String]): ReactiveInfluxDb =
    new ActorSystemReactiveInfluxDb(name, username, password, this)

  override def execute[R <: ReactiveInfluxCommand](request: R): Future[request.TResult] = {
    val httpRequest = request.httpRequest
    log.debug(s"${request.getClass.getSimpleName} HTTP ${httpRequest.method.name} ${httpRequest.uri}")
    http.singleRequest(httpRequest).map { httpResponse =>
      log.debug(s"Response: $httpResponse")
      request(httpResponse)
    }
  }
}

class ActorSystemReactiveInfluxDb(dbName: String,
                                  dbUsername: Option[String],
                                  dbPassword: Option[String],
                                  core: ReactiveInfluxCore) extends ReactiveInfluxDb {
  private implicit def executionContext: ExecutionContext = core.executionContext

  override def create(failIfExists: Boolean): Future[Unit] =
    core.execute(new CreateDatabaseCommand(core.config.uri, dbName, failIfExists))
  override def drop(failIfNotExists: Boolean): Future[Unit] =
    core.execute(new DropDatabaseCommand(core.config.uri, dbName, failIfNotExists))

  override def write(point: PointNoTime): Future[Unit] = write(point, WriteParameters())
  override def write(point: PointNoTime, params: WriteParameters): Future[Unit] = write(Seq(point))
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
      throw new ReactiveInfluxException("No results returned!")

    if (results.size > 1)
      throw new ReactiveInfluxException(s"Too many results returned! [${results.size}]")

    results.head
  }
  override def query(qs: Iterable[Query], params: QueryParameters): Future[Seq[QueryResult]] = ???
}

object ActorSystemReactiveInflux {
  def apply(actorSystem: ActorSystem, config: ReactiveInfluxConfig) =
    new ActorSystemReactiveInflux(actorSystem, config)
}