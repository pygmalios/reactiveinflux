package com.pygmalios.reactiveinflux.response

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpEntity.ChunkStreamPart
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.util.ByteString
import com.pygmalios.reactiveinflux.error._
import com.pygmalios.reactiveinflux.{ReactiveInfluxException, ReactiveInfluxResult}
import org.slf4j.LoggerFactory
import spray.json.{JsArray, JsString, _}

import scala.concurrent.Await
import scala.concurrent.duration._

class ReactiveInfluxJsonResultException(val errors: Set[ReactiveInfluxError]) extends ReactiveInfluxException(errors.mkString(","))

abstract class JsonResponse[+T](httpResponse: HttpResponse, actorSystem: ActorSystem) extends ReactiveInfluxResult[T] {
  import JsonResponse._

  private implicit def actorSys = actorSystem
  private implicit val actorMaterializer = ActorMaterializer()

  protected val results: JsArray = {
    httpResponse.entity match {
      case HttpEntity.Strict(ContentTypes.`application/json`, byteString) => processByteString(byteString)
      case HttpEntity.Strict(ContentTypes.NoContentType, _) => JsArray() // Empty response should be OK
      case HttpEntity.Chunked(ContentTypes.`application/json`, chunks) => processChunks(chunks)
      case other => throw new ReactiveInfluxException(s"Invalid response entity! [$other]")
    }
  }

  protected def errorHandler: PartialFunction[ReactiveInfluxError, Option[ReactiveInfluxError]] = PartialFunction.empty

  private def processChunks(chunks: Source[ChunkStreamPart, Any]): JsArray = {
    val sink = Sink.fold[JsArray, JsArray](JsArray.empty)((a1, a2) => JsArray(a1.elements ++ a2.elements))

    val jsArrays = chunks.map { chunkStreamPart =>
      processByteString(chunkStreamPart.data())
    }

    // TODO: This is absulutely disgusting, fix it!
    Await.result(jsArrays.toMat(sink)(Keep.right).run(), 60.seconds)
  }

  private def processByteString(byteString: ByteString): JsArray = {
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
  }

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