package com.pygmalios.reactiveinflux.command.write

sealed abstract class Precision(val q: String) extends Serializable {
  def format(pointTime: PointTime): String
}
case object Nano extends Precision("n") {
  override def format(pointTime: PointTime): String = (pointTime.seconds*1000000000 + pointTime.nanos).toString
}
case object Micro extends Precision("u") {
  override def format(pointTime: PointTime): String = ((pointTime.seconds*1000000000 + pointTime.nanos) / 1000).toString
}
case object Milli extends Precision("ms") {
  override def format(pointTime: PointTime): String = ((pointTime.seconds*1000000000 + pointTime.nanos) / 1000000).toString
}
case object Second extends Precision("s") {
  override def format(pointTime: PointTime): String = pointTime.seconds.toString
}
case object Minute extends Precision("m") {
  override def format(pointTime: PointTime): String = (pointTime.seconds / 60).toString
}
case object Hour extends Precision("h") {
  override def format(pointTime: PointTime): String = (pointTime.seconds / 3600).toString
}
