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

  def queryToString(q: Map[String, String]): String =
    q.map { case(k, v) =>
      k + "=" + URLEncoder.encode(v, Charsets.UTF_8.name()).replaceAll(" ", "%20")
    }.mkString(if (q.isEmpty) "" else "?", "&", "")
}
