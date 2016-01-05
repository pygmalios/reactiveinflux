package com.pygmalios.reactiveinflux.command.write

import java.time.Instant

sealed abstract class Precision(val q: String) extends Serializable {
  def format(i: Instant): String
  protected def nano(i: Instant, divideBy: Int, digits: Int): String = {
    val t = i.getNano / divideBy
    if (i.getEpochSecond > 0)
      i.getEpochSecond.toString + t.formatted(s"%0${digits}d")
    else
      t.toString
  }
}
case object Nano extends Precision("n") {
  override def format(i: Instant): String = nano(i, 1, 9)
}
case object Micro extends Precision("u") {
  override def format(i: Instant): String = nano(i, 1000, 6)
}
case object Milli extends Precision("ms") {
  override def format(i: Instant): String = nano(i, 1000000, 3)
}
case object Second extends Precision("s") {
  override def format(i: Instant): String = i.getEpochSecond.toString
}
case object Minute extends Precision("m") {
  override def format(i: Instant): String = (i.getEpochSecond / 60).toString
}
case object Hour extends Precision("h") {
  override def format(i: Instant): String = (i.getEpochSecond / 3600).toString
}
