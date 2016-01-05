package com.pygmalios.reactiveinflux.response

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import com.pygmalios.reactiveinflux.error._
import com.pygmalios.reactiveinflux.{ReactiveInfluxException, ReactiveInfluxResult}
import org.slf4j.LoggerFactory
import spray.json.{JsArray, JsString, _}

class ReactiveInfluxJsonResultException(val errors: Set[ReactiveInfluxError]) extends ReactiveInfluxException(errors.mkString(","))

abstract class JsonResponse[+T](httpResponse: HttpResponse) extends ReactiveInfluxResult[T] {
  import JsonResponse._

  protected val results: JsArray = {
    httpResponse.entity match {
      case HttpEntity.Strict(ContentTypes.`application/json`, byteString) =>
        val jsonBody = byteString.decodeString("UTF8").parseJson.asJsObject
        if (httpResponse.status.isSuccess()) {
          jsonBody.fields("results") match {
            case rs: JsArray =>
              val stringErrors = rs.elements.map(_.asJsObject).flatMap(_.fields.get("error")).flatMap {
                case JsString(reason) => Some(reason)
                case other =>
                  log.warn(s"Unknown error reason. [$other]")
                  None
              }
              processErrors(stringErrors)
              rs

            case other => throw new ReactiveInfluxException(s"Invalid JSON response! results field expected. [$other]")
          }
        }
        else {
          jsonBody.fields.get("error") match {
            case Some(JsString(reason)) =>
              processErrors(Seq(reason))
              JsArray()
            case _ => throw new ReactiveInfluxException(s"Not a successful response! [$httpResponse]")
          }
        }
      case HttpEntity.Strict(ContentTypes.NoContentType, _) => JsArray() // Empty response should be OK
      case other => throw new ReactiveInfluxException(s"Invalid response entity! [$other]")
    }
  }

  protected def errorHandler: PartialFunction[ReactiveInfluxError, Option[ReactiveInfluxError]] = PartialFunction.empty

  private def processErrors(stringErrors: Iterable[String]): Unit = {
    val errors = stringErrors.map(ReactiveInfluxError.apply).flatMap(errorHandler.applyOrElse(_, defaultErrorHandler)).toSet
    if (errors.nonEmpty)
      throw new ReactiveInfluxJsonResultException(errors)
  }
}

private object JsonResponse {
  val log = LoggerFactory.getLogger(JsonResponse.getClass)
  val defaultErrorHandler: PartialFunction[ReactiveInfluxError, Option[ReactiveInfluxError]] = {
    case error => Some(error)
  }
}