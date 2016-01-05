package com.pygmalios.reactiveinflux.impl

class EscapedString(unescaped: String) extends BaseEscapedString(unescaped)
class EscapedStringWithEquals(unescaped: String) extends BaseEscapedString(unescaped) {
  override val escaped = super.escaped.replace("=", "\\=")
}

abstract class BaseEscapedString(val unescaped: String) extends Serializable {
  def escaped: String = unescaped.replace(" ", "\\ ").replace(",", "\\,")

  def canEqual(other: Any): Boolean = other.isInstanceOf[EscapedString]

  override def equals(other: Any): Boolean = other match {
    case that: EscapedString =>
      (that canEqual this) &&
        unescaped == that.unescaped
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(unescaped)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString = escaped
}