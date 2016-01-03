package com.pygmalios.reactiveinflux.command

import java.time.{ZoneOffset, OffsetDateTime}

import akka.http.scaladsl.model.{HttpMethods, Uri}
import com.pygmalios.reactiveinflux.model.{Point, PointNoTime}
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

  behavior of "prec"

  it should "use provided precision" in new TestScope {
    assert(cmd(precision = Some(Second)).prec == Second)
  }

  it should "use nanosecond as default precision" in new TestScope {
    assert(cmd(precision = Some(Nano)).prec == Nano)
  }

  behavior of "query"

  it should "have db query" in new TestScope {
    assertQuery(cmd(), WriteCommand.dbQ, dbName)
  }

  it should "have retentionPolicy query" in new TestScope {
    assertQuery(cmd(retentionPolicy = Some("a")), WriteCommand.retentionPolicyQ, "a")
  }

  it should "have username query" in new TestScope {
    assertQuery(cmd(username = Some("a")), WriteCommand.usernameQ, "a")
  }

  it should "have password query" in new TestScope {
    assertQuery(cmd(password = Some("a")), WriteCommand.passwordQ, "a")
  }

  it should "have precision query" in new TestScope {
    assertQuery(cmd(precision = Some(Minute)), WriteCommand.precisionQ, Minute.q)
  }

  it should "have consistency query" in new TestScope {
    assertQuery(cmd(consistency = Some(Quorum)), WriteCommand.consistencyQ, Quorum.q)
  }

  behavior of "timestampToLine"

  it should "append nothing if no time is provided" in new TestScope {
    val sb = new StringBuilder
    WriteCommand.timestampToLine(Point("a", Map.empty, Map.empty), Nano, sb)
    assert(sb.isEmpty)
  }

  it should "append time" in new TestScope {
    val time = OffsetDateTime.of(1983, 1, 10, 11, 42, 0, 0, ZoneOffset.UTC).toInstant
    val sb = new StringBuilder
    WriteCommand.timestampToLine(Point(time, "a", Map.empty, Map.empty), Milli, sb)
    assert(sb.toString == " 411046920000")
  }
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

  def assertQuery(cmd: WriteCommand, key: String, value: String) =
    assert(cmd.query.get(key).contains(value), cmd.httpRequest.uri)
}