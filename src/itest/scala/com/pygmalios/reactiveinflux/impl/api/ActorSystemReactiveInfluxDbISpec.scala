package com.pygmalios.reactiveinflux.impl.api

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.pygmalios.reactiveinflux.api.ReactiveinfluxResultError
import com.pygmalios.reactiveinflux.api.result.errors.{DatabaseAlreadyExists, DatabaseNotFound, ReactiveinfluxError}
import com.pygmalios.reactiveinflux.itest.ITestConfig
import org.scalatest.concurrent.{AsyncAssertions, IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}

import scala.concurrent.Future

class ActorSystemReactiveInfluxDbISpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with FunSuiteLike with BeforeAndAfterAll with ScalaFutures with IntegrationPatience with AsyncAssertions {
  def this() = this(ActorSystem("ActorSystemReactiveInfluxDbISpec", ITestConfig.reactiveInfluxConfig.reactiveinflux))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  test("Create and drop test DB") {
    val testScope = new TestScope
    import testScope._

    try {
      db.create().futureValue
    }
    finally {
      db.drop().futureValue
    }
  }

  test("Create should fail if DB already exists") {
    val testScope = new TestScope
    import testScope._

    try {
      db.create(failIfExists = true).futureValue
      assertError(db.create(failIfExists = true), classOf[DatabaseAlreadyExists])
    }
    finally {
      db.drop().futureValue
    }
  }

  test("Create should not fail if DB already exists") {
    val testScope = new TestScope
    import testScope._

    try {
      db.create(failIfExists = true).futureValue
      db.create(failIfExists = false).futureValue
    }
    finally {
      db.drop().futureValue
    }
  }

  test("Drop should not fail if DB doesn't already exists") {
    val testScope = new TestScope
    import testScope._

    db.drop(failIfNotExists = false).futureValue
    db.drop(failIfNotExists = false).futureValue
  }

  test("Drop should fail if DB doesn't already exists") {
    val testScope = new TestScope
    import testScope._

    db.drop(failIfNotExists = false).futureValue
    assertError(db.drop(failIfNotExists = true), classOf[DatabaseNotFound], "database not found: ActorSystemReactiveInfluxDbISpec")
  }

  private class TestScope {
    val client = new ActorSystemReactiveInfluxClient(system, ITestConfig.reactiveInfluxConfig)
    val db = new ActorSystemReactiveInfluxDb("ActorSystemReactiveInfluxDbISpec", client)

    def assertError(f: => Future[_], error: Class[_ <: ReactiveinfluxError], message: String): Unit =
      assertError(f, error, Some(message))
    def assertError(f: => Future[_], error: Class[_ <: ReactiveinfluxError], message: Option[String] = None): Unit = {
      whenReady(f.failed) {
        case ex: ReactiveinfluxResultError =>
          ex.errors.find(_.getClass == error) match {
            case Some(e) if !message.contains(e.message) => fail(s"Expected error message [${message.get}] got [${e.message}]")
            case None => fail(s"Expected error not found. [$ex]")
            case _ =>
          }
        case other => fail(s"Unexpected exception. [$other]")
      }
    }
  }
}
