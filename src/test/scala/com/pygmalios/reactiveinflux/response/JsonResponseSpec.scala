//package com.pygmalios.reactiveinflux.response
//
//import akka.actor.ActorSystem
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.model.headers.RawHeader
//import akka.stream.scaladsl.Source
//import akka.testkit.{ImplicitSender, TestKit}
//import akka.util.ByteString
//import org.junit.runner.RunWith
//import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
//import org.scalatest.junit.JUnitRunner
//import org.scalatest.{BeforeAndAfterAll, FlatSpecLike}
//import spray.json.JsArray
//
//@RunWith(classOf[JUnitRunner])
//class JsonResponseSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
//  with FlatSpecLike with BeforeAndAfterAll with ScalaFutures with IntegrationPatience {
//  def this() = this(ActorSystem("JsonResponseSpec"))
//  override def afterAll {
//    TestKit.shutdownActorSystem(system)
//  }
//
//  behavior of "results"
//
//  it should "just handle chunked response of query with 100 points" in {
//    val response = new TestJsonResponse(JsonResponseSpec.response1, system)
//    assert(response.result.elements.size == 1)
//  }
//}
//
//object JsonResponseSpec {
//  val data1 =
//    """{"results":[{"series":[{"name":"m1","columns":["time","fk"],"values":[["1983-01-10T11:42:08.013Z",-1],["1983-01-10T11:42:09.013Z",-1],["1983-01-10T11:42:10.013Z",-1],["1983-01-10T11:42:11.013Z",-1],["1983-01-10T11:42:12.013Z",-1],["1983-01-10T11:42:13.013Z",-1],["1983-01-10T11:42:14.013Z",-1],["1983-01-10T11:42:15.013Z",-1],["1983-01-10T11:42:16.013Z",-1],["1983-01-10T11:42:17.013Z",-1],["1983-01-10T11:42:18.013Z",-1],["1983-01-10T11:42:19.013Z",-1],["1983-01-10T11:42:20.013Z",-1],["1983-01-10T11:42:21.013Z",-1],["1983-01-10T11:42:22.013Z",-1],["1983-01-10T11:42:23.013Z",-1],["1983-01-10T11:42:24.013Z",-1],["1983-01-10T11:42:25.013Z",-1],["1983-01-10T11:42:26.013Z",-1],["1983-01-10T11:42:27.013Z",-1],["1983-01-10T11:42:28.013Z",-1],["1983-01-10T11:42:29.013Z",-1],["1983-01-10T11:42:30.013Z",-1],["1983-01-10T11:42:31.013Z",-1],["1983-01-10T11:42:32.013Z",-1],["1983-01-10T11:42:33.013Z",-1],["1983-01-10T11:42:34.013Z",-1],["1983-01-10T11:42:35.013Z",-1],["1983-01-10T11:42:36.013Z",-1],["1983-01-10T11:42:37.013Z",-1],["1983-01-10T11:42:38.013Z",-1],["1983-01-10T11:42:39.013Z",-1],["1983-01-10T11:42:40.013Z",-1],["1983-01-10T11:42:41.013Z",-1],["1983-01-10T11:42:42.013Z",-1],["1983-01-10T11:42:43.013Z",-1],["1983-01-10T11:42:44.013Z",-1],["1983-01-10T11:42:45.013Z",-1],["1983-01-10T11:42:46.013Z",-1],["1983-01-10T11:42:47.013Z",-1],["1983-01-10T11:42:48.013Z",-1],["1983-01-10T11:42:49.013Z",-1],["1983-01-10T11:42:50.013Z",-1],["1983-01-10T11:42:51.013Z",-1],["1983-01-10T11:42:52.013Z",-1],["1983-01-10T11:42:53.013Z",-1],["1983-01-10T11:42:54.013Z",-1],["1983-01-10T11:42:55.013Z",-1],["1983-01-10T11:42:56.013Z",-1],["1983-01-10T11:42:57.013Z",-1],["1983-01-10T11:42:58.013Z",-1],["1983-01-10T11:42:59.013Z",-1],["1983-01-10T11:43:00.013Z",-1],["1983-01-10T11:43:01.013Z",-1],["1983-01-10T11:43:02.013Z",-1],["1983-01-10T11:43:03.013Z",-1],["1983-01-10T11:43:04.013Z",-1],["1983-01-10T11:43:05.013Z",-1],["1983-01-10T11:43:06.013Z",-1],["1983-01-10T11:43:07.013Z",-1],["1983-01-10T11:43:08.013Z",-1],["1983-01-10T11:43:09.013Z",-1],["1983-01-10T11:43:10.013Z",-1],["1983-01-10T11:43:11.013Z",-1],["1983-01-10T11:43:12.013Z",-1],["1983-01-10T11:43:13.013Z",-1],["1983-01-10T11:43:14.013Z",-1],["1983-01-10T11:43:15.013Z",-1],["1983-01-10T11:43:16.013Z",-1],["1983-01-10T11:43:17.013Z",-1],["1983-01-10T11:43:18.013Z",-1],["1983-01-10T11:43:19.013Z",-1],["1983-01-10T11:43:20.013Z",-1],["1983-01-10T11:43:21.013Z",-1],["1983-01-10T11:43:22.013Z",-1],["1983-01-10T11:43:23.013Z",-1],["1983-01-10T11:43:24.013Z",-1],["1983-01-10T11:43:25.013Z",-1],["1983-01-10T11:43:26.013Z",-1],["1983-01-10T11:43:27.013Z",-1],["1983-01-10T11:43:28.013Z",-1],["1983-01-10T11:43:29.013Z",-1],["1983-01-10T11:43:30.013Z",-1],["1983-01-10T11:43:31.013Z",-1],["1983-01-10T11:43:32.013Z",-1],["1983-01-10T11:43:33.013Z",-1],["1983-01-10T11:43:34.013Z",-1],["1983-01-10T11:43:35.013Z",-1],["1983-01-10T11:43:36.013Z",-1],["1983-01-10T11:43:37.013Z",-1],["1983-01-10T11:43:38.013Z",-1],["1983-01-10T11:43:39.013Z",-1],["1983-01-10T11:43:40.013Z",-1],["1983-01-10T11:43:41.013Z",-1],["1983-01-10T11:43:42.013Z",-1],["1983-01-10T11:43:43.013Z",-1],["1983-01-10T11:43:44.013Z",-1],["1983-01-10T11:43:45.013Z",-1],["1983-01-10T11:43:46.013Z",-1],["1983-01-10T11:43:47.013Z",-1]]}]}]}"""
//  val response1 = HttpResponse(
//    StatusCodes.OK,
//    List(
//      RawHeader("Request-Id", "65f77dd0-decb-11e5-8227-000000000000"),
//      RawHeader("X-Influxdb-Version", "0.10.1"),
//      RawHeader("Date", "Mon, 29 Feb 2016 10:01:27 GMT")),
//    HttpEntity.Chunked.fromData(ContentTypes.`application/json`, Source(List(ByteString(data1)))),
//    HttpProtocols.`HTTP/1.1`)
//}
//
//private class TestJsonResponse(httpResponse: HttpResponse, actorSystem: ActorSystem)
//  extends JsonResponse[JsArray](httpResponse, actorSystem) {
//  override def result = results
//}