package com.pygmalios.reactiveinflux.command.write

import java.net.URI

import com.pygmalios.reactiveinflux.{PointNoTime, ReactiveInfluxDbName}
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import play.api.http._
import play.api.libs.ws.{WSClient, WSRequestHolder}

@RunWith(classOf[JUnitRunner])
class WriteCommandSpec extends FlatSpec {
  behavior of "method"

  it should "use POST method" in new TestScope {
    verify(cmd().httpRequest(ws)).withMethod("POST")
  }

  behavior of "path"

  it should "have /write path" in new TestScope {
    cmd().httpRequest(ws)
    verify(ws).url("http://something/write?db=test")
  }

  behavior of "prec"

  it should "use provided precision" in new TestScope {
    assert(cmd(precision = Some(Second)).prec == Second)
  }

  it should "use millisecond as default precision" in new TestScope {
    assert(cmd(precision = Some(Milli)).prec == Milli)
  }

  behavior of "query"

  it should "have db query" in new TestScope {
    assertQuery(cmd(), WriteCommand.dbQ, dbName.value)
  }

  it should "have retentionPolicy query" in new TestScope {
    assertQuery(cmd(retentionPolicy = Some("a")), WriteParameters.retentionPolicyQ, "a")
  }

  it should "have precision query" in new TestScope {
    assertQuery(cmd(precision = Some(Minute)), WriteParameters.precisionQ, Minute.q)
  }

  it should "have consistency query" in new TestScope {
    assertQuery(cmd(consistency = Some(Quorum)), WriteParameters.consistencyQ, Quorum.q)
  }

  behavior of "entity"

  it should "contain binary content" in new TestScope {
    verify(cmd(points = Seq(PointSpec.point1, PointSpec.point2)).httpRequest(ws))
      .withHeaders("Content-Type" -> "application/octet-stream")
  }

  it should "contain point lines" in new TestScope {
    verify(cmd(points = Seq(PointSpec.point1, PointSpec.point2)).httpRequest(ws))
      .withBody(
        Matchers.eq("m1 fk=-1i 411046927013000000\nm2,tk1=tv1,tk2=tv2 fk=true,fk2=1,fk3=\"abcXYZ\" 411046927016000000"))(any[Writeable[String]](), any[ContentTypeOf[String]]())
  }
}

private class TestScope extends MockitoSugar {
  val dbName = ReactiveInfluxDbName("test")
  val baseUri = new URI("http://something/")

  val ws = mock[WSClient]
  val wsRequest = mock[WSRequestHolder]

  when(ws.url("http://something/write?db=test")).thenReturn(wsRequest)
  when(wsRequest.withHeaders(HeaderNames.CONTENT_TYPE -> MimeTypes.BINARY)).thenReturn(wsRequest)
  when(wsRequest.withMethod(HttpVerbs.POST)).thenReturn(wsRequest)
  when(wsRequest.withBody(anyString())(any[Writeable[String]](), any[ContentTypeOf[String]]())).thenReturn(wsRequest)

  def cmd(baseUri: URI = baseUri,
          dbName: ReactiveInfluxDbName = dbName,
          points: Seq[PointNoTime] = Seq.empty,
          retentionPolicy: Option[String] = None,
          precision: Option[Precision] = None,
          consistency: Option[Consistency] = None) =
    new WriteCommand(
      baseUri,
      dbName,
      points,
      WriteParameters(retentionPolicy, precision, consistency))

  def assertQuery(cmd: WriteCommand, key: String, value: String) = {
    assert(cmd.query.get(key).flatten == Some(value), s"${cmd.query}, $key, $value")
  }
}