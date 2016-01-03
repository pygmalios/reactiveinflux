package com.pygmalios.reactiveinflux.impl.response

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import com.pygmalios.reactiveinflux.api.ReactiveinfluxException
import com.pygmalios.reactiveinflux.api.response.errors.{DatabaseAlreadyExists, OtherError, ReactiveinfluxError}
import com.pygmalios.reactiveinflux.core.ReactiveinfluxResponse
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

          val errors = stringErrors.map(errorHandler.applyOrElse(_, DefaultErrorHandler.apply)).flatten.toSet
          if (errors.nonEmpty)
            throw new ReactiveinfluxJsonResultException(errors)
        case other => throw new ReactiveinfluxException(s"Invalid JSON response! results field expected. [$other]")
      }
    case other => throw new ReactiveinfluxException(s"Invalid response! [$other]")
  }

  protected def errorHandler: PartialFunction[String, Option[ReactiveinfluxError]] = PartialFunction.empty
}

private object JsonResponse {
  val log = LoggerFactory.getLogger(JsonResponse.getClass)
}

private object DefaultErrorHandler {
  def apply(error: String): Option[ReactiveinfluxError] = {
    val result = error match {
      case DatabaseAlreadyExists.message => DatabaseAlreadyExists
      case other => OtherError(other)
    }
    Some(result)
  }
}