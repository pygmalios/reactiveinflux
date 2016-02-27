package com.pygmalios.reactiveinflux.command.write

import org.joda.time.Instant

sealed abstract class Precision(val q: String) extends Serializable {
  def format(i: Instant): String
}
// TODO: Java 7 supports up to milli only
//case object Nano extends Precision("n") {
//  override def format(i: Instant): String = nano(i, 1, 9)
//}
//case object Micro extends Precision("u") {
//  override def format(i: Instant): String = nano(i, 1000, 6)
//}
case object Milli extends Precision("ms") {
  override def format(i: Instant): String = i.getMillis.toString
}
case object Second extends Precision("s") {
  override def format(i: Instant): String = (i.getMillis / 1000).toString
}
case object Minute extends Precision("m") {
  override def format(i: Instant): String = (i.getMillis / 60000).toString
}
case object Hour extends Precision("h") {
  override def format(i: Instant): String = (i.getMillis / 3600000).toString
}
