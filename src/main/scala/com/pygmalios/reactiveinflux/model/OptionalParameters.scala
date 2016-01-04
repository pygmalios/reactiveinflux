package com.pygmalios.reactiveinflux.model

trait OptionalParameters extends Serializable {
  def params: Map[String, String]
}

object OptionalParameters {
  def apply(param: (String, Option[String])*): Map[String, String] =
    param.collect {
      case (k, Some(v)) => k -> v
    }.toMap
}