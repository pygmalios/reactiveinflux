//package com.pygmalios.reactiveinflux.impl
//
//import akka.actor.ActorSystem
//import com.pygmalios.reactiveinflux.itest.ITestConfig
//import org.junit.runner.RunWith
//import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
//import org.scalatest.junit.JUnitRunner
//import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}
//
//@RunWith(classOf[JUnitRunner])
//class ActorSystemReactiveInfluxClientISpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
//  with FunSuiteLike with BeforeAndAfterAll with ScalaFutures with IntegrationPatience {
//  def this() = this(ActorSystem("ActorSystemReactiveInfluxClientISpec", ITestConfig.reactiveInfluxConfig.reactiveinflux))
//
//  override def afterAll {
//    TestKit.shutdownActorSystem(system)
//  }
//
////  test("Send ping to test InfluxDB") {
////    val testScope = new TestScope
////    import testScope._
////
////    // Execute
////    assert(client.ping().futureValue.influxDbVersion == "0.10.1")
////  }
////
////  private class TestScope {
////    val client = new ActorSystemReactiveInflux(system, ITestConfig.reactiveInfluxConfig)
////  }
//}