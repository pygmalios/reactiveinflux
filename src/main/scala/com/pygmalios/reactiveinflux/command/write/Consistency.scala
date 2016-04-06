package com.pygmalios.reactiveinflux.command.write

sealed abstract class Consistency(val q: String) extends Serializable
case object One extends Consistency("one")
case object Quorum extends Consistency("quorum")
case object All extends Consistency("all")
case object Any extends Consistency("any")

object Consistency {
  def apply(c: String): Consistency = c match {
    case One.q => One
    case Quorum.q => Quorum
    case All.q => All
    case Any.q => Any
  }
}