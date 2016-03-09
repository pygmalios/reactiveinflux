package com.pygmalios.reactiveinflux.impl

object URIUtils {
  def queryToString(q: Map[String, String]): String =
    q.map { case(k, v) =>
      k + "=" + v.toString
    }.mkString("?", "&", "")
}
