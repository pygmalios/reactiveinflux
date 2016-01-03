package com.pygmalios.reactiveinflux.api.result.errors

import scala.util.matching.Regex

sealed trait ReactiveinfluxError {
  def message: String
}

object ReactiveinfluxError {
  val patterns: Map[Regex, (String) => ReactiveinfluxError] = Map(
    "database already exists".r -> DatabaseAlreadyExists.apply,
    "database not found: (.)+".r -> DatabaseNotFound.apply
  )

  def apply(message: String): ReactiveinfluxError =
    patterns.collectFirst {
      case (regex, factory) if regex.unapplySeq(message).isDefined => factory(message)
    }.getOrElse(OtherErrorType(message))

}

case class DatabaseAlreadyExists(message: String) extends ReactiveinfluxError
case class DatabaseNotFound(message: String) extends ReactiveinfluxError
case class OtherErrorType(message: String) extends ReactiveinfluxError