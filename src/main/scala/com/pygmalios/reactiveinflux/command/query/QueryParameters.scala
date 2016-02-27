package com.pygmalios.reactiveinflux.command.query

import com.pygmalios.reactiveinflux.ReactiveInfluxException
import com.pygmalios.reactiveinflux.impl.OptionalParameters
import org.joda.time.Instant

trait QueryParameters extends OptionalParameters {
  def epoch: Option[Epoch]
  def chunkSize: Option[Int]
}

sealed abstract class Epoch(val q: String) extends TimeFormat with Serializable {
  protected def toInstant(value: Value, c: Int): Instant = value match {
    case BigDecimalValue(n) => new Instant(n.toLong * c)
    case other => throw new ReactiveInfluxException(s"Not a number! [$other]")
  }
}
// TODO: Java 7 supports up to milli only
//case object NanoEpoch extends Epoch("ns") {
//  def apply(value: Value): Instant = super.small(value, 1000000000)
//}
//case object MicroEpoch extends Epoch("u") {
//  def apply(value: Value): Instant = super.small(value, 1000000)
//}
case object MilliEpoch extends Epoch("ms") {
  def apply(value: Value): Instant = super.toInstant(value, 1)
}
case object SecondEpoch extends Epoch("s") {
  def apply(value: Value): Instant = super.toInstant(value, 1000)
}
case object MinuteEpoch extends Epoch("m") {
  def apply(value: Value): Instant = super.toInstant(value, 60000)
}
case object HourEpoch extends Epoch("h") {
  def apply(value: Value): Instant = super.toInstant(value, 3600000)
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
