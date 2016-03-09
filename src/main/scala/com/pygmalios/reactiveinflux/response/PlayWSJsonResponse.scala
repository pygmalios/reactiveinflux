package com.pygmalios.reactiveinflux.response

import com.pygmalios.reactiveinflux.error.ReactiveInfluxError
import com.pygmalios.reactiveinflux.{ReactiveInfluxException, ReactiveInfluxResult}
import play.api.http.Status
import play.api.libs.json.{JsDefined, JsArray, JsObject, JsString}
import play.api.libs.ws.WSResponse

class ReactiveInfluxJsonResultException(val errors: Set[ReactiveInfluxError]) extends ReactiveInfluxException(errors.mkString(","))

abstract class PlayWSJsonResponse[+T](wsResponse: WSResponse) extends ReactiveInfluxResult[T] {
  import PlayWSJsonResponse._

  protected def results: Seq[JsObject] = {
    wsResponse.status match {
      case Status.OK =>
        try {
          // Get body as JSON
          wsResponse.json match {
            // Get it as JSON object
            case jsObject: JsObject =>
              // Get "results" field
              jsObject \ resultsField match {
                case JsDefined(resultsArray: JsArray) =>
                  // Process all result item
                  val results = resultsArray.value.map {
                      // Get result item as JSON object
                      case result: JsObject => result
                      case other =>
                        throw new ReactiveInfluxException(s"Ivalid result item! [$other]")
                  }

                  // Get and process errors from results (if any)
                  processErrors(errorsFromResults(results))

                  results
                case _ =>
                  throw new ReactiveInfluxException("No results JSON field!")
              }
            case _ =>
              throw new ReactiveInfluxException(s"Not a JSON object!")
          }
        }
        catch {
          case ex: Exception =>
            throw new ReactiveInfluxException(s"Invalid JSON response! [$wsResponse]", ex)
        }

      case _ =>
        throw new ReactiveInfluxException(s"Not a successful response! [$wsResponse]")
    }
  }

  protected def errorHandler: PartialFunction[ReactiveInfluxError, Option[ReactiveInfluxError]] = PartialFunction.empty

  private def processErrors(stringErrors: Iterable[String]): Unit = {
    val errors = stringErrors.map(ReactiveInfluxError.apply).flatMap(errorHandler.applyOrElse(_, defaultErrorHandler)).toSet
    if (errors.nonEmpty)
      throw new ReactiveInfluxJsonResultException(errors)
  }

  private def errorsFromResults(results: Seq[JsObject]): Seq[String] = {
    results.flatMap {
      _ \ errorField match {
        case JsDefined(error: JsString) => Some(error.value)
        case _ => None
      }
    }
  }
}

object PlayWSJsonResponse {
  val resultsField = "results"
  val errorField = "error"
  val defaultErrorHandler: PartialFunction[ReactiveInfluxError, Option[ReactiveInfluxError]] = {
    case error => Some(error)
  }
}