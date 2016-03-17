package com.pygmalios.reactiveinflux.command.query

import java.net.URI

import com.pygmalios.reactiveinflux.ReactiveInflux._
import com.pygmalios.reactiveinflux.ReactiveInfluxResult
import com.pygmalios.reactiveinflux.command.write._
import org.junit.runner.RunWith
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import play.api.http._
import play.api.libs.ws.{WSResponse, WSRequestHolder, WSClient}

@RunWith(classOf[JUnitRunner])
class BaseQueryCommandSpec extends FlatSpec {
  behavior of "query"

  it should "have username in query" in {
    new TestQueryCommand(new URI("http://something/"))
  }
}

private class TestQueryCommand(baseUri: URI) extends BaseQueryCommand(baseUri) {
  override type TResult = this.type
  override protected def responseFactory(httpResponse: WSResponse): ReactiveInfluxResult[TResult] = ???
  override def httpRequest(ws: WSClient): WSRequestHolder = ???
}

