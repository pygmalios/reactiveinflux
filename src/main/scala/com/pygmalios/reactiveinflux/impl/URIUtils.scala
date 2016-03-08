package com.pygmalios.reactiveinflux.impl

object URIUtils {
  def queryToString(q: Map[String, Option[String]]): String =
    q.filter(_._2.isDefined).map { case(k, v) =>
      k + "=" + v.get.toString
    }.mkString("?", "&", "")
}
