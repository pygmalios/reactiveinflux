package com.pygmalios.reactiveinflux.command.query

import com.pygmalios.reactiveinflux.command.Precision
import com.pygmalios.reactiveinflux.model.OptionalParameters

trait QueryParameters extends OptionalParameters

object QueryParameters {
  val epochQ = "epoch"
  val chunkSizeQ = "chunk_size"

  def apply(epoch: Option[Precision] = None,
            chunkSize: Option[Int] = None): QueryParameters =
    SimpleQueryParameters(epoch, chunkSize)
}

case class SimpleQueryParameters(epoch: Option[Precision] = None,
                                 chunkSize: Option[Int] = None) extends QueryParameters {
  import QueryParameters._

  override def params = OptionalParameters(
    epochQ -> epoch.map(_.q),
    chunkSizeQ -> chunkSize.map(_.toString)
  )
}
