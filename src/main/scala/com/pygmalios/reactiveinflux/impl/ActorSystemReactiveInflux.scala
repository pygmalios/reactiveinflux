package com.pygmalios.reactiveinflux.impl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.pygmalios.reactiveinflux._
import com.pygmalios.reactiveinflux.command.{CreateDatabase, DropDatabase, PingCommand}
import com.pygmalios.reactiveinflux.model.PointNoTime

import scala.concurrent.{ExecutionContext, Future}

private[reactiveinflux] class ActorSystemReactiveInflux(actorSystem: ActorSystem, val config: ReactiveInfluxConfig)
  extends ReactiveInflux with ReactiveInfluxCore with Logging {

  protected implicit def system: ActorSystem = actorSystem
  protected implicit def executionContext: ExecutionContext = actorSystem.dispatcher
  protected implicit val materializer: ActorMaterializer = ActorMaterializer(Some(ActorMaterializerSettings(actorSystem)))
  protected val http = Http(actorSystem)

  override def close(): Unit = {
    actorSystem.terminate()
  }

  override def ping(waitForLeaderSec: Option[Int]) = execute(new PingCommand(config.uri))

  override def database(name: String): ReactiveInfluxDb = new ActorSystemReactiveInfluxDb(name, this)

  override def execute[R <: ReactiveInfluxCommand](request: R): Future[request.TResult] = {
    val httpRequest = request.httpRequest
    log.debug(s"${request.getClass.getSimpleName} HTTP ${httpRequest.method.name} ${httpRequest.uri}")
    http.singleRequest(httpRequest).map { httpResponse =>
      log.debug(s"Response: $httpResponse")
      request(httpResponse)
    }
  }
}

private[impl] class ActorSystemReactiveInfluxDb(dbName: String, client: ActorSystemReactiveInflux) extends ReactiveInfluxDb {
  import client._

  override def create(failIfExists: Boolean): Future[Unit] = execute(new CreateDatabase(config.uri, dbName, failIfExists))
  override def drop(failIfNotExists: Boolean): Future[Unit] = execute(new DropDatabase(config.uri, dbName, failIfNotExists))
  override def write(point: PointNoTime): Future[Unit] = ???
  override def write(points: Iterable[PointNoTime]): Future[Unit] = ???
}

private[reactiveinflux] object ActorSystemReactiveInflux {
  def apply(actorSystem: ActorSystem, config: ReactiveInfluxConfig) =
    new ActorSystemReactiveInflux(actorSystem, config)
}