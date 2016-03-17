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

    def withAuthority(username: String, password: String): URI = {
      new URI(uri.getScheme, s"$username:$password", uri.getPath, uri.getQuery, uri.getFragment)
    }
  }

  class URIPath(parts: Seq[String]) {
    // Check
    parts.foreach { part =>
      if (part.contains("/"))
        throw new IllegalArgumentException(s"Path part cannot contain /! [$parts]")
    }

    override def toString = parts.mkString("/")
  }

  object URIPath {
    def apply(path: String) = new URIPath(path.split("/"))
  }

  class URIQueryString(val items: Map[String, Option[String]]) {
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
