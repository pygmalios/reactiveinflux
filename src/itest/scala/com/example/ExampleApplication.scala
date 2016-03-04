package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import com.pygmalios.reactiveinflux.command.query.BaseQueryCommand
import com.pygmalios.reactiveinflux.response.EmptyJsonResponse
import com.pygmalios.reactiveinflux.itest.ITestConfig
import com.pygmalios.reactiveinflux.{ReactiveInfluxDbParams, ReactiveInflux, ReactiveInfluxCore}
import org.scalatest.FunSuite
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class ExampleApplication extends FunSuite with ScalaFutures with IntegrationPatience {
  test("Execute custom command") {
    val reactiveInflux = ReactiveInflux(config = Some(ITestConfig.config))
    try {
      implicit val params = ReactiveInfluxDbParams("ExampleApplicatixon")
      val db = reactiveInflux.database
      try {
        val core = reactiveInflux.asInstanceOf[ReactiveInfluxCore]
        whenReady(core.execute(new CustomQueryCommand(core.config.uri)).failed) { ex =>
        }
      }
      finally {
        db.drop()
      }
    }
    finally {
      reactiveInflux.close()
    }
  }
}

class CustomQueryCommand(baseUri: Uri) extends BaseQueryCommand(baseUri) {
  override type TResult = Unit
  override protected def responseFactory(httpResponse: HttpResponse, actorSystem: ActorSystem) =
    new CustomQueryCommandResponse(httpResponse, actorSystem)
  override val httpRequest = HttpRequest(uri = qUri("WHATEVER"))
}

class CustomQueryCommandResponse(httpResponse: HttpResponse, actorSystem: ActorSystem)
  extends EmptyJsonResponse(httpResponse, actorSystem)