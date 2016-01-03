package com.pygmalios.reactiveinflux.impl.response

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import com.pygmalios.reactiveinflux.api.{ReactiveinfluxResponse, ReactiveinfluxException}
import com.pygmalios.reactiveinflux.api.result.errors._
import org.slf4j.LoggerFactory
import spray.json.{JsArray, JsString, _}

class ReactiveinfluxJsonResultException(val errors: Set[ReactiveinfluxError]) extends ReactiveinfluxException(errors.mkString(","))

abstract class JsonResponse[+T](httpResponse: HttpResponse) extends ReactiveinfluxResponse[T] {
  import JsonResponse._

  if (!httpResponse.status.isSuccess())
    throw new ReactiveinfluxException(s"Not a successful response! [$httpResponse]")

  httpResponse.entity match {
    case HttpEntity.Strict(ContentTypes.`application/json`, byteString) =>
      val jsonBody = byteString.decodeString("UTF8").parseJson.asJsObject
      jsonBody.fields("results") match {
        case JsArray(results) =>
          val stringErrors = results.map(_.asJsObject).flatMap(_.fields.get("error")).flatMap {
            case JsString(reason) => Some(reason)
            case other =>
              log.warn(s"Unknown error reason. [$other]")
              None
          }

          val errors = stringErrors.map(ReactiveinfluxError.apply).flatMap(errorHandler.applyOrElse(_, defaultErrorHandler)).toSet
          if (errors.nonEmpty)
            throw new ReactiveinfluxJsonResultException(errors)
        case other => throw new ReactiveinfluxException(s"Invalid JSON response! results field expected. [$other]")
      }
    case other => throw new ReactiveinfluxException(s"Invalid response! [$other]")
  }

  protected def errorHandler: PartialFunction[ReactiveinfluxError, Option[ReactiveinfluxError]] = PartialFunction.empty
}

private object JsonResponse {
  val log = LoggerFactory.getLogger(JsonResponse.getClass)
  val defaultErrorHandler: PartialFunction[ReactiveinfluxError, Option[ReactiveinfluxError]] = {
    case error => Some(error)
  }
}