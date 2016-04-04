package com.example

import java.net.URI

import com.pygmalios.reactiveinflux.ReactiveInflux.ReactiveInfluxDbName
import com.pygmalios.reactiveinflux.command.query.BaseQueryCommand
import com.pygmalios.reactiveinflux.itest.ITestConfig
import com.pygmalios.reactiveinflux.response.EmptyJsonResponse
import com.pygmalios.reactiveinflux.{ReactiveInflux, ReactiveInfluxCore}
import org.scalatest.FunSuite
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.libs.ws.{WSClient, WSResponse}

class ExampleApplication extends FunSuite with ScalaFutures with IntegrationPatience {
  test("Execute custom command") {
    val reactiveInflux = ReactiveInflux(config = Some(ITestConfig.config))
    try {
      implicit val dbName = ReactiveInfluxDbName("ExampleApplicatixon")
      val db = reactiveInflux.database
      try {
        val core = reactiveInflux.asInstanceOf[ReactiveInfluxCore]
        whenReady(core.execute(new CustomQueryCommand(core.config.url)).failed) { ex =>
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

class CustomQueryCommand(baseUri: URI) extends BaseQueryCommand(baseUri) {
  override type TResult = Unit
  override protected def responseFactory(wsResponse: WSResponse) =  new CustomQueryCommandResponse(wsResponse)
  override def httpRequest(ws: WSClient) = ws.url(qUri("WHATEVER").toString)
}

class CustomQueryCommandResponse(wsResponse: WSResponse) extends EmptyJsonResponse(wsResponse)