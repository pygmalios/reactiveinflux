package com.pygmalios.reactiveinflux.command

import akka.http.scaladsl.model._
import akka.util.ByteString
import com.pygmalios.reactiveinflux.ReactiveInfluxCommand
import com.pygmalios.reactiveinflux.response.EmptyJsonResponse

sealed abstract class Precision(val value: String)
case object Nano extends Precision("n")
case object Micro extends Precision("u")
case object Milli extends Precision("ms")
case object Second extends Precision("s")
case object Minute extends Precision("m")
case object Hour extends Precision("h")

sealed abstract class Consistency(val value: String)
case object One extends Consistency("one")
case object Quorum extends Consistency("quorum")
case object All extends Consistency("all")
case object Any extends Consistency("any")

class WriteCommand(baseUri: Uri,
                   dbName: String,
                   retentionPolicy: Option[String],
                   username: Option[String],
                   password: Option[String],
                   precision: Option[Precision],
                   consistency: Option[Consistency]) extends ReactiveInfluxCommand {
  import WriteCommand._

  override type TResult = Unit
  protected val uriWithPath = baseUri.withPath(path)
  override protected def responseFactory(httpResponse: HttpResponse) = new WriteResponse(httpResponse)
  override val httpRequest = {
    val lines = HttpEntity.Strict(ContentTypes.`application/octet-stream`, ByteString(""))
    HttpRequest(
      method  = HttpMethods.POST,
      uri     = uriWithPath.withQuery(query),
      entity  = lines
    )
  }

  private def query = {
    val qMap = Map(
      queryDbQ -> Some(dbName),
      retentionPolicyQ -> retentionPolicy,
      usernameQ -> username,
      passwordQ -> password,
      precisionQ -> precision.map(_.value),
      consistencyQ -> consistency.map(_.value)
    ).collect {
      case (k, Some(v)) => k -> v
    }

    Uri.Query(qMap)
  }
}

object WriteCommand {
  val path = Uri.Path("/write")
  val queryDbQ = "db"
  val retentionPolicyQ = "rp"
  val usernameQ = "u"
  val passwordQ = "p"
  val precisionQ = "precision"
  val consistencyQ = "consistency"
}

private[reactiveinflux] class WriteResponse(httpResponse: HttpResponse) extends EmptyJsonResponse(httpResponse)