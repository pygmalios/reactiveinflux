package com.pygmalios.reactiveinflux.command.write

import com.pygmalios.reactiveinflux.impl.OptionalParameters

trait WriteParameters extends OptionalParameters {
  def retentionPolicy: Option[String]
  def precision: Option[Precision]
  def consistency: Option[Consistency]
}

object WriteParameters {
  val retentionPolicyQ = "rp"
  val precisionQ = "precision"
  val consistencyQ = "consistency"

  def apply(retentionPolicy: Option[String] = None,
            precision: Option[Precision] = None,
            consistency: Option[Consistency] = None): WriteParameters =
    SimpleWriteParameters(retentionPolicy, precision, consistency)
}

case class SimpleWriteParameters(retentionPolicy: Option[String],
                                 precision: Option[Precision],
                                 consistency: Option[Consistency]) extends WriteParameters {
  import WriteParameters._

  override def params = OptionalParameters(
    retentionPolicyQ -> retentionPolicy,
    precisionQ -> precision.map(_.q),
    consistencyQ -> consistency.map(_.q)
  )
}