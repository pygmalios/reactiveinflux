package com.pygmalios.reactiveinflux.impl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.pygmalios.reactiveinflux._
import com.pygmalios.reactiveinflux.command.{CreateDatabaseCommand, DropDatabaseCommand, PingCommand, WriteCommand}
import com.pygmalios.reactiveinflux.model.{PointNoTime, WriteParameters}

import scala.concurrent.{ExecutionContext, Future}

class ActorSystemReactiveInflux(actorSystem: ActorSystem, val config: ReactiveInfluxConfig)
  extends ReactiveInflux with ReactiveInfluxCore with Logging {

  protected implicit def system: ActorSystem = actorSystem
  protected implicit def executionContext: ExecutionContext = actorSystem.dispatcher
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
}

object ActorSystemReactiveInflux {
  def apply(actorSystem: ActorSystem, config: ReactiveInfluxConfig) =
    new ActorSystemReactiveInflux(actorSystem, config)
}