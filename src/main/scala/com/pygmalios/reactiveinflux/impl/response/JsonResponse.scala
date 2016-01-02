package com.pygmalios.reactiveinflux.impl.response

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import com.pygmalios.reactiveinflux.api.ReactiveinfluxException
import com.pygmalios.reactiveinflux.core.ReactiveinfluxResponse
import org.slf4j.LoggerFactory
import spray.json.{JsArray, JsString, _}

class ReactiveinfluxJsonResultException(val error: String) extends ReactiveinfluxException(error)

abstract class JsonResponse[+T](httpResponse: HttpResponse) extends ReactiveinfluxResponse[T] {
  import JsonResponse._

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
          if (errorReasons.nonEmpty)
            throw new ReactiveinfluxJsonResultException(errorReasons.mkString(","))
        case other => throw new ReactiveinfluxException(s"Invalid JSON response! results field expected. [$other]")
      }
    case other => throw new ReactiveinfluxException(s"Invalid response! [$other]")
  }
}

private object JsonResponse {
  val log = LoggerFactory.getLogger(JsonResponse.getClass)
}
