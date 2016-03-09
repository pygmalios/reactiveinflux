package com.pygmalios.reactiveinflux.impl

import java.net.URI

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class URIUtilsSpec extends FlatSpec {
  behavior of "appendPath"

  it should "strip /" in {
    assert(URIUtils.appendPath(new URI("http://x/"), "/a").toString == "http://x/a")
  }

  behavior of "queryToString"

  it should "create empty query string" in {
    assert(URIUtils.queryToString(Map.empty) == "")
  }

  it should "create query string with single item" in {
    assert(URIUtils.queryToString(Map("a" -> "b")) == "?a=b")
  }

  it should "create query string with two items" in {
    assert(URIUtils.queryToString(Map("a" -> "b", "c" -> "d")) == "?a=b&c=d")
  }
}
