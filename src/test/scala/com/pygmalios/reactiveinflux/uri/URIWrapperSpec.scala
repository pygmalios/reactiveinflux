package com.pygmalios.reactiveinflux.uri

import java.net.URI

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class URIWrapperSpec extends FlatSpec {
  import URIWrapperSpec._

  behavior of "withPath"

  it should "append x path to URI" in {
    assert(uri.withPath("x").getPath == "/x")
  }

  it should "append /x path to URI" in {
    assert(uri.withPath("/x").getPath == "/x")
  }

  it should "append x/y path to URI" in {
    assert(uri.withPath("x/y").getPath == "/x/y")
  }

  behavior of "withQuery"

  it should "append empty query string" in {
    assert(uri.withQuery(URIQueryString.empty).getQuery == null)
  }

  it should "append single query string" in {
    assert(uri.withQuery(Map("a" -> Some("b"))).getQuery == "a=b")
  }

  it should "append double query string" in {
    assert(uri.withQuery(Map("a" -> Some("1"), "b" -> Some("2"))).getQuery == "a=1&b=2")
  }

  it should "append double query string with none" in {
    assert(uri.withQuery(Map("a" -> None, "b" -> Some("2"))).getQuery == "b=2")
  }

  behavior of "all combined"

  it should "append path and query string" in {
    assert(uri.withPath("/x").withQuery(Map("a" -> Some("1"), "b" -> Some("2"))).toString == "http://something/x?a=1&b=2")
  }
}

object URIWrapperSpec {
  val uri = new URI("http://something/")
}