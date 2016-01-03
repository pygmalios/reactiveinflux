package com.pygmalios.reactiveinflux.impl.response

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import com.pygmalios.reactiveinflux.error._
import com.pygmalios.reactiveinflux.{ReactiveInfluxException, ReactiveInfluxResult}
import org.slf4j.LoggerFactory
import spray.json.{JsArray, JsString, _}

class ReactiveInfluxJsonResultException(val errors: Set[ReactiveInfluxError]) extends ReactiveInfluxException(errors.mkString(","))

abstract class JsonResult[+T](httpResponse: HttpResponse) extends ReactiveInfluxResult[T] {
  import JsonResult._

  if (!httpResponse.status.isSuccess())
    throw new ReactiveInfluxException(s"Not a successful response! [$httpResponse]")

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

          val errors = stringErrors.map(ReactiveInfluxError.apply).flatMap(errorHandler.applyOrElse(_, defaultErrorHandler)).toSet
          if (errors.nonEmpty)
            throw new ReactiveInfluxJsonResultException(errors)
        case other => throw new ReactiveInfluxException(s"Invalid JSON response! results field expected. [$other]")
      }
    case other => throw new ReactiveInfluxException(s"Invalid response! [$other]")
  }

  protected def errorHandler: PartialFunction[ReactiveInfluxError, Option[ReactiveInfluxError]] = PartialFunction.empty
}

private object JsonResult {
  val log = LoggerFactory.getLogger(JsonResult.getClass)
  val defaultErrorHandler: PartialFunction[ReactiveInfluxError, Option[ReactiveInfluxError]] = {
    case error => Some(error)
  }
}