package com.pygmalios.reactiveinflux.command

sealed abstract class Consistency(val q: String)
case object One extends Consistency("one")
case object Quorum extends Consistency("quorum")
case object All extends Consistency("all")
case object Any extends Consistency("any")
