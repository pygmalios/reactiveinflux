package com.example

import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import com.pygmalios.reactiveinflux.command.BaseQueryCommand
import com.pygmalios.reactiveinflux.impl.response.EmptyJsonResult
import com.pygmalios.reactiveinflux.itest.ITestConfig
import com.pygmalios.reactiveinflux.{ReactiveInflux, ReactiveInfluxCore}
import org.scalatest.FunSuite
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class ExampleApplication extends FunSuite with ScalaFutures with IntegrationPatience {
  test("Execute custom command") {
    val reactiveInflux = ReactiveInflux(config = Some(ITestConfig.config))
    try {
      val db = reactiveInflux.database("ExampleApplicatixon")
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
  override protected def responseFactory(httpResponse: HttpResponse) = new CustomQueryCommandResult(httpResponse)
  override val httpRequest = HttpRequest(uri = qUri("WHATEVER"))
}

class CustomQueryCommandResult(httpResponse: HttpResponse) extends EmptyJsonResult(httpResponse)