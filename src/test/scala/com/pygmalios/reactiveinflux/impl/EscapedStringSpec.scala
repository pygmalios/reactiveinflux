package com.pygmalios.reactiveinflux.impl

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EscapedStringSpec extends FlatSpec {
  behavior of "EscapedString"

  it should "replace spaces" in {
    assert(new EscapedString(" ").escaped == "\\ ")
  }

  it should "replace commas" in {
    assert(new EscapedString(",").escaped == "\\,")
  }

  it should "not replace equals" in {
    assert(new EscapedString("=").escaped == "=")
  }

  it should "return escaped string in toString" in {
    assert(new EscapedString("a.b.c,1 Fg8=\\a").toString == "a.b.c\\,1\\ Fg8=\\a")
  }

  behavior of "EscapedStringWithEquals"

  it should "replace equals" in {
    assert(new EscapedStringWithEquals("=").escaped == "\\=")
  }

  it should "return escaped string in toString" in {
    assert(new EscapedStringWithEquals("a.b.c,1 Fg8=\\a").toString == "a.b.c\\,1\\ Fg8\\=\\a")
  }
}
