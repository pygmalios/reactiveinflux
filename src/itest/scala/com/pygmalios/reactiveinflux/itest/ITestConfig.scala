package com.pygmalios.reactiveinflux.itest

import com.pygmalios.reactiveinflux.ReactiveInfluxConfig
import com.typesafe.config.ConfigFactory

object ITestConfig {
  lazy val config = ConfigFactory.load("itest.conf")
  lazy val reactiveInfluxConfig = ReactiveInfluxConfig(Some(config))
}