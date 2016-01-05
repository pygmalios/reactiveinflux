package com.pygmalios.reactiveinflux.command.query

import java.time.Instant

import com.pygmalios.reactiveinflux.ReactiveInfluxException
import com.pygmalios.reactiveinflux.command.write.Precision
import com.pygmalios.reactiveinflux.impl.OptionalParameters

trait QueryParameters extends OptionalParameters {
  def epoch: Option[Epoch]
  def chunkSize: Option[Int]
}

sealed abstract class Epoch(val q: String) extends TimeFormat with Serializable {
  protected def small(value: Value, c: Long): Instant = toInstant(value, c) { l =>
    Instant.ofEpochSecond(l / c, l % c)
  }

  protected def big(value: Value, c: Long): Instant = toInstant(value, c) { l =>
    Instant.ofEpochSecond(l * c)
  }

  private def toInstant(value: Value, c: Long)(f: (Long) => Instant): Instant = value match {
    case BigDecimalValue(n) => f(n.toLong)
    case other => throw new ReactiveInfluxException(s"Not a number! [$other]")
  }
}
case object NanoEpoch extends Epoch("ns") {
  def apply(value: Value): Instant = super.small(value, 1000000000)
}
case object MicroEpoch extends Epoch("u") {
  def apply(value: Value): Instant = super.small(value, 1000000)
}
case object MilliEpoch extends Epoch("ms") {
  def apply(value: Value): Instant = super.small(value, 1000)
}
case object SecondEpoch extends Epoch("s") {
  def apply(value: Value): Instant = super.small(value, 1)
}
case object MinuteEpoch extends Epoch("m") {
  def apply(value: Value): Instant = super.big(value, 60)
}
case object HourEpoch extends Epoch("h") {
  def apply(value: Value): Instant = super.big(value, 3600)
}

object QueryParameters {
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
