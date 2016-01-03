package com.pygmalios.reactiveinflux.impl.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.pygmalios.reactiveinflux.api.model.PointNoTime
import com.pygmalios.reactiveinflux.api.{ReactiveInfluxClient, ReactiveInfluxDb}
import com.pygmalios.reactiveinflux.core.{ReactiveinfluxCoreClient, ReactiveinfluxRequest}
import com.pygmalios.reactiveinflux.impl.request.Ping
import com.pygmalios.reactiveinflux.impl.request.query.{CreateDatabase, DropDatabase}
import com.pygmalios.reactiveinflux.impl.{Logging, ReactiveInfluxConfig}

import scala.concurrent.{ExecutionContext, Future}

class ActorSystemReactiveInfluxClient(actorSystem: ActorSystem, val config: ReactiveInfluxConfig)
  extends ReactiveInfluxClient with ReactiveinfluxCoreClient with Logging {

  protected implicit def system: ActorSystem = actorSystem
  protected implicit def executionContext: ExecutionContext = actorSystem.dispatcher
  protected implicit val materializer: ActorMaterializer = ActorMaterializer(Some(ActorMaterializerSettings(actorSystem)))
  protected val http = Http(actorSystem)

  override def close(): Unit = {
    actorSystem.terminate()
  }

  override def ping(waitForLeaderSec: Option[Int]) = execute(new Ping(config.uri))

  override def database(name: String): ReactiveInfluxDb = new ActorSystemReactiveInfluxDb(name, this)

  override def execute[R <: ReactiveinfluxRequest](request: R): Future[request.TResponse] = {
    val httpRequest = request.httpRequest
    log.debug(s"${request.getClass.getSimpleName} HTTP ${httpRequest.method.name} ${httpRequest.uri}")
    http.singleRequest(httpRequest).map { httpResponse =>
      log.debug(s"Response: $httpResponse")
      request(httpResponse)
    }
  }
}

class ActorSystemReactiveInfluxDb(dbName: String, client: ActorSystemReactiveInfluxClient) extends ReactiveInfluxDb {
  import client._

  override def create(failIfExists: Boolean): Future[Unit] = execute(new CreateDatabase(config.uri, dbName, failIfExists))
  override def drop(failIfNotExists: Boolean): Future[Unit] = execute(new DropDatabase(config.uri, dbName, failIfNotExists))
  override def write(point: PointNoTime): Future[Unit] = ???
  override def write(points: Iterable[PointNoTime]): Future[Unit] = ???
}