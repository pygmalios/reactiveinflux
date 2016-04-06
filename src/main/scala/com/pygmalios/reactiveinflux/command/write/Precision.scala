package com.pygmalios.reactiveinflux.command.write

import com.pygmalios.reactiveinflux.PointTime
import com.pygmalios.reactiveinflux.command.query._

sealed abstract class Precision(val q: String) extends Serializable {
  def round(pointTime: PointTime): PointTime
  def format(pointTime: PointTime): String
}
case object Nano extends Precision("n") {
  override def round(pointTime: PointTime): PointTime = pointTime
  override def format(pointTime: PointTime): String = (pointTime.seconds*1000000000 + pointTime.nanos).toString
}
case object Micro extends Precision("u") {
  override def round(pointTime: PointTime): PointTime = PointTime.ofEpochSecond(pointTime.seconds, (pointTime.nanos / 1000) * 1000)
  override def format(pointTime: PointTime): String = ((pointTime.seconds*1000000000 + pointTime.nanos) / 1000).toString
}
case object Milli extends Precision("ms") {
  override def round(pointTime: PointTime): PointTime = PointTime.ofEpochSecond(pointTime.seconds, (pointTime.nanos / 1000000) * 1000000)
  override def format(pointTime: PointTime): String = ((pointTime.seconds*1000000000 + pointTime.nanos) / 1000000).toString
}
case object Second extends Precision("s") {
  override def round(pointTime: PointTime): PointTime = PointTime.ofEpochSecond(pointTime.seconds, 0)
  override def format(pointTime: PointTime): String = pointTime.seconds.toString
}
case object Minute extends Precision("m") {
  override def round(pointTime: PointTime): PointTime = PointTime.ofEpochSecond((pointTime.seconds / 60) * 60, 0)
  override def format(pointTime: PointTime): String = (pointTime.seconds / 60).toString
}
case object Hour extends Precision("h") {
  override def round(pointTime: PointTime): PointTime = PointTime.ofEpochSecond((pointTime.seconds / 3600) * 3600, 0)
  override def format(pointTime: PointTime): String = (pointTime.seconds / 3600).toString
}

object Precision {
  def apply(epoch: Epoch): Precision = epoch match {
    case NanoEpoch => Nano
    case MicroEpoch => Micro
    case MilliEpoch => Milli
    case SecondEpoch => Second
    case MinuteEpoch => Minute
    case HourEpoch => Hour
  }
}