package com.pygmalios.reactiveinflux.command

import akka.http.scaladsl.model.{HttpMethods, Uri}
import com.pygmalios.reactiveinflux.model.PointNoTime
import org.scalatest.FlatSpec

class WriteCommandSpec extends FlatSpec {
  behavior of "method"

  it should "use POST method" in new TestScope {
    assert(cmd().httpRequest.method == HttpMethods.POST)
  }

  behavior of "path"

  it should "have /write path" in new TestScope {
    assert(cmd().httpRequest.uri.path == Uri.Path("/write"))
  }

  behavior of "query"

  it should "have db query" in new TestScope {
    assertQ(cmd(), WriteCommand.dbQ, dbName)
  }

  it should "have retentionPolicy query" in new TestScope {
    assertQ(cmd(retentionPolicy = Some("a")), WriteCommand.retentionPolicyQ, "a")
  }

  it should "have username query" in new TestScope {
    assertQ(cmd(username = Some("a")), WriteCommand.usernameQ, "a")
  }

  it should "have password query" in new TestScope {
    assertQ(cmd(password = Some("a")), WriteCommand.passwordQ, "a")
  }

  it should "have precision query" in new TestScope {
    assertQ(cmd(precision = Some(Minute)), WriteCommand.precisionQ, Minute.q)
  }

  it should "have consistency query" in new TestScope {
    assertQ(cmd(consistency = Some(Quorum)), WriteCommand.consistencyQ, Quorum.q)
  }

  behavior of "entity"
}

private class TestScope {
  val dbName = "test"
  val baseUri = Uri("http://something/")
  def cmd(baseUri: Uri = baseUri,
          dbName: String = dbName,
          points: Seq[PointNoTime] = Seq.empty,
          retentionPolicy: Option[String] = None,
          username: Option[String] = None,
          password: Option[String] = None,
          precision: Option[Precision] = None,
          consistency: Option[Consistency] = None) =
    new WriteCommand(
      baseUri,
      dbName,
      points,
      retentionPolicy,
      username,
      password,
      precision,
      consistency)

  def assertQ(cmd: WriteCommand, key: String, value: String) =
    assert(cmd.httpRequest.uri.query().get(key).contains(value), cmd.httpRequest.uri)
}