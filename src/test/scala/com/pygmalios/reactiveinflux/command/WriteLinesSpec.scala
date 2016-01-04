package com.pygmalios.reactiveinflux.command

import java.time.{OffsetDateTime, ZoneOffset}

import com.pygmalios.reactiveinflux.model._
import org.scalatest.FlatSpec

class WriteLinesSpec extends FlatSpec {
  behavior of "timestampToLine"

  it should "append nothing if no time is provided" in new TestScope {
    wl.timestampToLine(Point("a", Map.empty, Map.empty), Nano, sb)
    assert(sb.isEmpty)
  }

  it should "append time" in new TestScope {
    val time = OffsetDateTime.of(1983, 1, 10, 11, 42, 0, 0, ZoneOffset.UTC).toInstant
    wl.timestampToLine(Point(time, "a", Map.empty, Map.empty), Milli, sb)
    assert(sb.toString == " 411046920000")
  }

  behavior of "fieldValueToLine"

  it should "convert string value as is" in new TestScope {
    assert(wl.fieldValueToLine(StringFieldValue("a")) == "a")
  }

  it should "convert double value as is" in new TestScope {
    assert(wl.fieldValueToLine(DoubleFieldValue(-13.5879215d)) == "-13.5879215")
  }

  it should "convert long value with i appended" in new TestScope {
    assert(wl.fieldValueToLine(LongFieldValue(42)) == "42i")
  }

  it should "convert true boolean to true" in new TestScope {
    assert(wl.fieldValueToLine(BooleanFieldValue(true)) == "true")
  }

  it should "convert false boolean to false" in new TestScope {
    assert(wl.fieldValueToLine(BooleanFieldValue(false)) == "false")
  }

  behavior of "fieldsToLine"

  it should "append nothing if no fields are provided" in new TestScope {
    wl.fieldsToLine(Map.empty, sb)
    assert(sb.isEmpty)
  }

  it should "append single long field" in new TestScope {
    wl.fieldsToLine(Map("a" -> LongFieldValue(666)), sb)
    assert(sb.toString == " a=666i")
  }

  it should "append double and boolean fields" in new TestScope {
    wl.fieldsToLine(Map("l" -> DoubleFieldValue(0.1), "b" -> BooleanFieldValue(false)), sb)
    assert(sb.toString == " l=0.1,b=false")
  }

  private class TestScope {
    val sb = new StringBuilder
    val wl = new WriteLines(Seq.empty, Nano)
  }
}
