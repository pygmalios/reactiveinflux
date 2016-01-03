package com.pygmalios.reactiveinflux.api.response.errors

sealed trait ReactiveinfluxError extends Serializable {
  def message: String
}
case object DatabaseAlreadyExists extends ReactiveinfluxError {
  val message = "database already exists"
}
case class OtherError(message: String) extends ReactiveinfluxError
