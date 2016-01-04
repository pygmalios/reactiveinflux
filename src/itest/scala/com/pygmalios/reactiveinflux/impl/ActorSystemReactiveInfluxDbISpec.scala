package com.pygmalios.reactiveinflux.impl

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.pygmalios.reactiveinflux.ReactiveInfluxResultError
import com.pygmalios.reactiveinflux.error.{DatabaseAlreadyExists, DatabaseNotFound, ReactiveInfluxError}
import com.pygmalios.reactiveinflux.itest.ITestConfig
import com.pygmalios.reactiveinflux.model.PointSpec
import org.scalatest.concurrent.{AsyncAssertions, IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike}

import scala.concurrent.Future

class ActorSystemReactiveInfluxDbISpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with FlatSpecLike with BeforeAndAfterAll with ScalaFutures with IntegrationPatience with AsyncAssertions {
  def this() = this(ActorSystem("ActorSystemReactiveInfluxDbISpec", ITestConfig.reactiveInfluxConfig.reactiveinflux))

  implicit private def executionContext = system.dispatcher

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  behavior of "create"

  it should "create a test DB" in new TestScope {
    try {
      db.create().futureValue
    }
    finally {
      db.drop().futureValue
    }
  }

  it should "fail if DB already exists" in new TestScope {
    try {
      db.create(failIfExists = true).futureValue
      assertError(db.create(failIfExists = true), classOf[DatabaseAlreadyExists], "database already exists")
    }
    finally {
      db.drop().futureValue
    }
  }

  it should "not fail if DB already exists" in new TestScope {
    try {
      db.create(failIfExists = true).futureValue
      db.create(failIfExists = false).futureValue
    }
    finally {
      db.drop().futureValue
    }
  }

  behavior of "drop"

  it should "not fail if DB doesn't already exists" in new TestScope {
    db.drop(failIfNotExists = false).futureValue
    db.drop(failIfNotExists = false).futureValue
  }

  it should "fail if DB doesn't already exists" in new TestScope {
    db.drop(failIfNotExists = false).futureValue
    assertError(db.drop(failIfNotExists = true), classOf[DatabaseNotFound], "database not found: ActorSystemReactiveInfluxDbISpec")
  }

  behavior of "write"

  it should "write a single point" in new TestScope {
    withDb { db =>
      db.write(PointSpec.point1)
    }
  }

  it should "write two points" in new TestScope {
    withDb { db =>
      db.write(Seq(PointSpec.point1, PointSpec.point2))
    }
  }

  private class TestScope {
    val client = new ActorSystemReactiveInflux(system, ITestConfig.reactiveInfluxConfig)
    val db = new ActorSystemReactiveInfluxDb("ActorSystemReactiveInfluxDbISpec", None, None, client)

    def withDb(action: (ActorSystemReactiveInfluxDb) => Any): Unit = {
      val result = db.create().map { _ =>
        action(db)
      }.map { _ =>
        db.drop()
      }
      result.futureValue
    }

    def assertError(f: => Future[_], error: Class[_ <: ReactiveInfluxError], message: String): Unit =
      assertError(f, error, Some(message))
    def assertError(f: => Future[_], error: Class[_ <: ReactiveInfluxError], message: Option[String] = None): Unit = {
      whenReady(f.failed) {
        case ex: ReactiveInfluxResultError =>
          ex.errors.find(_.getClass == error) match {
            case Some(e) if !message.contains(e.message) => fail(s"Expected error message [${message.map(s => s)}] got [${e.message}]")
            case None => fail(s"Expected error not found. [$ex]")
            case _ =>
          }
        case other => fail(s"Unexpected exception. [$other]")
      }
    }
  }
}
