package com.pygmalios.reactiveinflux.command.write

import java.net.URI

import com.pygmalios.reactiveinflux.ReactiveInflux.{DbName, DbPassword, DbUsername}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import play.api.libs.ws.{InMemoryBody, WSClient}

@RunWith(classOf[JUnitRunner])
class WriteCommandSpec extends FlatSpec {
  behavior of "method"

  it should "use POST method" in new TestScope {
    assert(cmd().httpRequest(ws).method == "POST")
  }

  behavior of "path"

  it should "have /write path" in new TestScope {
    assert(cmd().httpRequest(ws).uri.getPath == "/write")
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
    assertQuery(cmd(), WriteCommand.dbQ, dbName)
  }

  it should "have retentionPolicy query" in new TestScope {
    assertQuery(cmd(retentionPolicy = Some("a")), WriteParameters.retentionPolicyQ, "a")
  }

  it should "have username query" in new TestScope {
    assertQuery(cmd(username = Some("a")), WriteCommand.usernameQ, "a")
  }

  it should "have password query" in new TestScope {
    assertQuery(cmd(password = Some("a")), WriteCommand.passwordQ, "a")
  }

  it should "have precision query" in new TestScope {
    assertQuery(cmd(precision = Some(Minute)), WriteParameters.precisionQ, Minute.q)
  }

  it should "have consistency query" in new TestScope {
    assertQuery(cmd(consistency = Some(Quorum)), WriteParameters.consistencyQ, Quorum.q)
  }

  behavior of "entity"

  it should "contain binary content" in new TestScope {
    val headers = cmd(points = Seq(PointSpec.point1, PointSpec.point2)).httpRequest(ws).headers
    assert(headers.get("Content-Type").get == Seq("application/octet-stream"))
  }

  it should "contain point lines" in new TestScope {
    cmd(points = Seq(PointSpec.point1, PointSpec.point2)).httpRequest(ws).body match {
      case InMemoryBody(bytes) =>
          val decoded = new String(bytes, "UTF8")
          assert(decoded == "m1 fk=-1i 411046927013000000\nm2,tk1=tv1,tk2=tv2 fk=true,fk2=1,fk3=\"abcXYZ\" 411046927016000000")
      case _ => fail("Invalit entity!")
    }
  }
}

private class TestScope extends MockitoSugar {
  val dbName = "test"
  val baseUri = new URI("http://something/")
  val ws = mock[WSClient]
  def cmd(baseUri: URI = baseUri,
          dbName: DbName = dbName,
          points: Seq[PointNoTime] = Seq.empty,
          retentionPolicy: Option[String] = None,
          username: Option[DbUsername] = None,
          password: Option[DbPassword] = None,
          precision: Option[Precision] = None,
          consistency: Option[Consistency] = None) =
    new WriteCommand(
      baseUri,
      dbName,
      username,
      password,
      points,
      WriteParameters(retentionPolicy,precision,consistency))

  def assertQuery(cmd: WriteCommand, key: String, value: String) =
    assert(cmd.query.get(key) == Some(value))
}