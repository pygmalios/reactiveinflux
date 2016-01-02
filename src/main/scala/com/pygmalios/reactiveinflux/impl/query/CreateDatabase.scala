package com.pygmalios.reactiveinflux.impl.query

import akka.http.scaladsl.model._
import com.pygmalios.reactiveinflux.api.{ReactiveinfluxException, ReactiveinfluxResultError}
import org.slf4j.LoggerFactory
import spray.json.{JsArray, JsString, _}

class CreateDatabase(baseUri: Uri, name: String) extends BaseQuery(baseUri) {
  import CreateDatabase._

  override type Response = Unit

  override val httpRequest = HttpRequest(uri = qUri(queryPattern.format(name)))

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

private object CreateDatabase {
  val log = LoggerFactory.getLogger(classOf[CreateDatabase])
  val queryPattern = "CREATE DATABASE %s"
}