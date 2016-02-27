package com.pygmalios.reactiveinflux.command.write

import com.pygmalios.reactiveinflux._
import org.joda.time.{DateTimeZone, DateTime}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class WriteLinesSpec extends FlatSpec {
  behavior of "timestampToLine"

  it should "append nothing if no time is provided" in new TestScope {
    wl.timestampToLine(Point("a", Map.empty, Map.empty), Milli, sb)
    assert(sb.isEmpty)
  }

  it should "append time" in new TestScope {
    wl.timestampToLine(Point(time, "a", Map.empty, Map.empty), Milli, sb)
    assert(sb.toString == " 411046920000")
  }

  behavior of "fieldValueToLine"

  it should "convert string value as is" in new TestScope {
    assert(wl.fieldValueToLine(StringFieldValue("a")) == "\"a\"")
  }

  it should "escape quotes in strings" in new TestScope {
    assert(wl.fieldValueToLine(StringFieldValue("""x"y"""")) == """"x\"y\""""")
  }

  it should "convert double value as is" in new TestScope {
    assert(wl.fieldValueToLine(BigDecimalFieldValue(-13.5879215d)) == "-13.5879215")
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
    wl.fieldsToLine(Map("l" -> BigDecimalFieldValue(0.1), "b" -> BooleanFieldValue(false)), sb)
    assert(sb.toString == " l=0.1,b=false")
  }

  behavior of "tagsToLine"

  it should "append nothing if no tags are provided" in new TestScope {
    wl.tagsToLine(Map.empty, sb)
    assert(sb.isEmpty)
  }

  it should "append a single tag" in new TestScope {
    wl.tagsToLine(Map("a" -> "1"), sb)
    assert(sb.toString == ",a=1")
  }

  it should "append two tags" in new TestScope {
    wl.tagsToLine(Map("a" -> "1", "b" -> "2"), sb)
    assert(sb.toString == ",a=1,b=2")
  }

  behavior of "pointToLine"

  it should "append measurement, tags, fields and timestamp" in new TestScope {
    val point = Point(time, "m", Map("tk" -> "tv"), Map("fk" -> LongFieldValue(-1)))
    wl.pointToLine(point, Second, sb)
    assert(sb.toString() == "m,tk=tv fk=-1i 411046920")
  }

  behavior of "toString"

  it should "append two points separated by newline" in {
    val wl = new WriteLines(Seq(PointSpec.point1, PointSpec.point2), Milli)
    assert(wl.toString() == "m1 fk=-1i 411046920000\nm2,tk1=tv1,tk2=tv2 fk=true,fk2=1,fk3=\"abcXYZ\" 411046920003")
  }

  private class TestScope {
    val time = new DateTime(1983, 1, 10, 11, 42, 0, 0, DateTimeZone.UTC).toInstant
    val sb = new StringBuilder
    val wl = new WriteLines(Seq.empty, Milli)
  }
}
