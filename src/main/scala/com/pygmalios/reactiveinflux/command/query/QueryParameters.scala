package com.pygmalios.reactiveinflux.command.query

import com.pygmalios.reactiveinflux.ReactiveInfluxException
import com.pygmalios.reactiveinflux.command.write.PointTime
import com.pygmalios.reactiveinflux.impl.OptionalParameters

trait QueryParameters extends OptionalParameters {
  def epoch: Option[Epoch]
  def chunkSize: Option[Int]
}

sealed abstract class Epoch(val q: String) extends TimeFormat with Serializable {
  protected def toPointTime(value: Value, convert: (Long) => PointTime): PointTime = value match {
    case BigDecimalValue(n) => convert(n.toLong)
    case other => throw new ReactiveInfluxException(s"Not a number! [$other]")
  }
}
case object NanoEpoch extends Epoch("ns") {
  def apply(value: Value): PointTime = super.toPointTime(value, ns => PointTime.ofEpochSecond(ns / 1000000000, ns % 1000000000))
}
case object MicroEpoch extends Epoch("u") {
  def apply(value: Value): PointTime = super.toPointTime(value, u => PointTime.ofEpochSecond(u / 1000000, u % 1000000))
}
case object MilliEpoch extends Epoch("ms") {
  def apply(value: Value): PointTime = super.toPointTime(value, ms => PointTime.ofEpochSecond(ms / 1000, ms % 1000))
}
case object SecondEpoch extends Epoch("s") {
  def apply(value: Value): PointTime = super.toPointTime(value, s => PointTime.ofEpochSecond(s, 0))
}
case object MinuteEpoch extends Epoch("m") {
  def apply(value: Value): PointTime = super.toPointTime(value, m => PointTime.ofEpochSecond(m * 60, 0))
}
case object HourEpoch extends Epoch("h") {
  def apply(value: Value): PointTime = super.toPointTime(value, h => PointTime.ofEpochSecond(h * 3600, 0))
}

object QueryParameters {
  val dbQ = "db"
  val epochQ = "epoch"
  val chunkSizeQ = "chunk_size"

  def apply(epoch: Option[Epoch] = None,
            chunkSize: Option[Int] = None): QueryParameters =
    SimpleQueryParameters(epoch, chunkSize)
}

case class SimpleQueryParameters(epoch: Option[Epoch] = None,
                                 chunkSize: Option[Int] = None) extends QueryParameters {
  import QueryParameters._

  override def params = OptionalParameters(
    epochQ -> epoch.map(_.q),
    chunkSizeQ -> chunkSize.map(_.toString)
  )
}
