package com.pygmalios.reactiveinflux.impl.api.response

import com.pygmalios.reactiveinflux.api.response.PingResponse

case class SimplePingResponse(influxDbVersion: String) extends PingResponse
