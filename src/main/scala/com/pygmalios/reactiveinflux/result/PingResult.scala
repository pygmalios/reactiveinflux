package com.pygmalios.reactiveinflux.result

import com.pygmalios.reactiveinflux.ReactiveInfluxResult

trait PingResult extends Serializable {
  def influxDbVersion: String
}

private[reactiveinflux] case class SimplePingResult(influxDbVersion: String) extends PingResult with ReactiveInfluxResult[PingResult] {
  override def result: PingResult = this
}
