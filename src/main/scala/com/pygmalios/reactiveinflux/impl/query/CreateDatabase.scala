package com.pygmalios.reactiveinflux.impl.query

import akka.http.scaladsl.model._
import com.pygmalios.reactiveinflux.api.{ReactiveinfluxException, ReactiveinfluxResultError}
import spray.json.{JsArray, JsString, _}

class CreateDatabase(baseUri: Uri, name: String) extends BaseQuery {
  override type Response = Unit

  val httpRequest: HttpRequest = {
    val uri = baseUri.withPath(Uri.Path("/query")).withQuery(Uri.Query("q" -> ("CREATE DATABASE " + name)))
    HttpRequest(uri = uri)
  }

  override def apply(httpResponse: HttpResponse): Unit = {
    log.debug(s"CreateDatabase HTTP response. [$httpResponse]")
    httpResponse.entity match {
      case HttpEntity.Strict(ContentTypes.`application/json`, byteString) =>
        val jsonBody = byteString.decodeString("UTF8").parseJson.asJsObject
        jsonBody.fields("results") match {
          case JsArray(results) =>
            val errorReasons = results.map(_.asJsObject).flatMap(_.fields.get("error")).flatMap {
              case JsString(reason) => Some(reason)
              case other =>
                log.warn(s"Unknown error reason. [$other]")
                None
            }
            throw new ReactiveinfluxResultError(errorReasons.mkString(","), httpRequest)
          case other => throw new ReactiveinfluxException(s"Invalid JSON response! results field expected. [$other]")
        }
      case other => throw new ReactiveinfluxException(s"Invalid response! [$other]")
    }
  }
}