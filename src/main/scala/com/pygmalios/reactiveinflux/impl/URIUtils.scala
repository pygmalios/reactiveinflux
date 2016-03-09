package com.pygmalios.reactiveinflux.impl

import java.net.{URLEncoder, URI}

import com.google.common.base.Charsets
import play.utils.UriEncoding

object URIUtils {
  private val sep = "/"

  def appendPath(uri: URI, path: String): URI = {
    val encodedPath = UriEncoding.encodePathSegment(path.stripPrefix(sep), Charsets.UTF_8.name())
    new URI(uri.toString.stripSuffix(sep) + sep + encodedPath)
  }

  def appendQuery(uri: URI, qs: (String, String)*): URI =
    new URI(uri.toString + queryToString(qs:_*))

  def queryToString(qs: (String, String)*): String =
    qs.map { case(k, v) =>
      k + "=" + URLEncoder.encode(v, Charsets.UTF_8.name()).replaceAll(" ", "%20")
    }.mkString(if (qs.isEmpty) "" else "?", "&", "")
}
