package com.pygmalios.reactiveinflux.error

import scala.util.matching.Regex

sealed trait ReactiveInfluxError {
  def message: String
}

object ReactiveInfluxError {
  val patterns: Map[Regex, (String) => ReactiveInfluxError] = Map(
    "database already exists".r -> DatabaseAlreadyExists.apply,
    "database not found: (.)+".r -> DatabaseNotFound.apply
  )

  def apply(message: String): ReactiveInfluxError =
    patterns.collectFirst {
      case (regex, factory) if regex.unapplySeq(message).isDefined => factory(message)
    }.getOrElse(OtherErrorType(message))

}

case class DatabaseAlreadyExists(message: String) extends ReactiveInfluxError
case class DatabaseNotFound(message: String) extends ReactiveInfluxError
case class OtherErrorType(message: String) extends ReactiveInfluxError