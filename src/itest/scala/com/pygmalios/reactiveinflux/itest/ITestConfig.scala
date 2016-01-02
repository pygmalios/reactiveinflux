package com.pygmalios.reactiveinflux.itest

import com.pygmalios.reactiveinflux.impl.ReactiveInfluxConfig
import com.typesafe.config.ConfigFactory

object ITestConfig {
  lazy val reactiveInfluxConfig = ReactiveInfluxConfig(Some(ConfigFactory.load("itest.conf")))
}