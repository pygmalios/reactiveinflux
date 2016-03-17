package com.pygmalios.reactiveinflux

import java.net.{URI, URLEncoder}

import com.google.common.base.Charsets
import play.utils.UriEncoding

package object uri {
  implicit class URIWrapper(uri: URI) {
    def withPath(uriPath: URIPath): URI = {
      val encodedPath = UriEncoding.encodePathSegment(uriPath.toString.stripPrefix("/"), Charsets.UTF_8.name())
      new URI(uri.toString.stripSuffix("/") + "/" + encodedPath)
    }

    def withQuery(uriQueryString: URIQueryString): URI = new URI(uri.toString + uriQueryString.toString)
  }

  class URIPath(parts: Seq[String]) {
    // Check
    parts.foreach { part =>
      if (part.contains("/"))
        throw new IllegalArgumentException(s"Path part cannot contain /! [$parts]")
    }

    override def toString = parts.mkString("/")
  }

  implicit object URIPath {
    implicit def apply(path: String): URIPath = new URIPath(path.split("/"))
  }

  implicit class URIQueryString(val items: Map[String, Option[String]]) {
    def ++(queryString: URIQueryString): URIQueryString = new URIQueryString(items ++ queryString.items)

    override def toString: String =
      items.collect {
        case (k, Some(v)) =>
          k + "=" + URLEncoder.encode(v, Charsets.UTF_8.name()).replaceAll(" ", "%20")
      }.mkString(if (items.isEmpty) "" else "?", "&", "")
  }

  object URIQueryString {
    val empty = new URIQueryString(Map.empty)
  }
}
