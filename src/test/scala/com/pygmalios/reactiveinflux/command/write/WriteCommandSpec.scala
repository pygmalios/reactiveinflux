package com.pygmalios.reactiveinflux.command.write

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, Uri}
import com.pygmalios.reactiveinflux.ReactiveInflux.{DbName, DbPassword, DbUsername}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class WriteCommandSpec extends FlatSpec {
  behavior of "method"

  it should "use POST method" in new TestScope {
    assert(cmd().httpRequest.method == HttpMethods.POST)
  }

  behavior of "path"

  it should "have /write path" in new TestScope {
    assert(cmd().httpRequest.uri.path == Uri.Path("/write"))
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
    val entity = cmd(points = Seq(PointSpec.point1, PointSpec.point2)).httpRequest.entity
    assert(entity.getContentType() == ContentTypes.`application/octet-stream`)
  }

  it should "contain point lines" in new TestScope {
    cmd(points = Seq(PointSpec.point1, PointSpec.point2)).httpRequest.entity match {
      case HttpEntity.Strict(_, byteString) =>
          assert(byteString.decodeString("UTF8") == "m1 fk=-1i 411046920000\nm2,tk1=tv1,tk2=tv2 fk=true,fk2=1,fk3=\"abcXYZ\" 411046920003")
      case _ => fail("Invalit entity!")
    }
  }
}

private class TestScope {
  val dbName = "test"
  val baseUri = Uri("http://something/")
  def cmd(baseUri: Uri = baseUri,
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
    assert(cmd.query.get(key) == Some(value), cmd.httpRequest.uri)
}