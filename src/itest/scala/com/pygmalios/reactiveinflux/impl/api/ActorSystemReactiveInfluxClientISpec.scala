package com.pygmalios.reactiveinflux.impl.api

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}

class ActorSystemReactiveInfluxClientISpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with FunSuiteLike with BeforeAndAfterAll with ScalaFutures with IntegrationPatience {
  def this() = this(ActorSystem("ActorSystemReactiveInfluxClientISpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  test("Send ping to test InfluxDB") {
    // Prepare
    val client = new ActorSystemReactiveInfluxClient(system)

    // Execute
    assert(client.ping().futureValue.influxDbVersion == "0.9.6.1")
  }
}
