package com.pygmalios.reactiveinflux.impl

import java.net.URI

object URIUtils {
  private val sep = "/"

  def appendPath(uri: URI, path: String): URI =
    new URI(uri.toString.stripSuffix(sep) + sep + path.stripPrefix(sep))

  def queryToString(q: Map[String, String]): String =
    q.map { case(k, v) =>
      k + "=" + v.toString
    }.mkString(if (q.isEmpty) "" else "?", "&", "")
}
