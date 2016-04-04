package com.pygmalios.reactiveinflux.impl

import com.pygmalios.reactiveinflux.itest.ITestConfig
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PlayWSReactiveInfluxISpec extends FlatSpec with ScalaFutures {
  behavior of "ping"

  it should "send ping request to InfluxDB" in new TestScope {
    assert(reactiveInflux.ping().futureValue.influxDbVersion.startsWith("0.11"))
  }
}

private class TestScope {
  val reactiveInflux = new PlayWSReactiveInflux(ITestConfig.reactiveInfluxConfig)
}
