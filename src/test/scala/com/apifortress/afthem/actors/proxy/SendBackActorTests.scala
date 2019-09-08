package com.apifortress.afthem.actors.proxy

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.apifortress.afthem.{Metric, ReqResUtil}
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.messages.{BaseMessage, WebParsedResponseMessage}
import com.apifortress.afthem.messages.beans.{Header, HttpWrapper}
import org.junit.Assert._
import org.junit.Test

class SendBackActorTests {

  def createWrapper() : HttpWrapper =
     new HttpWrapper("http://foo.com",200,"GET",
      List[Header](new Header(ReqResUtil.HEADER_CONTENT_LENGTH,"123"),
        new Header(ReqResUtil.HEADER_CONTENT_ENCODING,"gzip"),
        new Header("x-key","ABC123")))
  @Test
  def testAdjustResponseHeaders() : Unit = {
    val httpWrapper = createWrapper()

    SendBackActor.adjustResponseHeaders(httpWrapper)
    assertEquals(2,httpWrapper.headers.size)
    assertEquals("application/octet-stream",httpWrapper.getHeader("content-type"))


    httpWrapper.setHeader("content-type","text/plain")
    SendBackActor.adjustResponseHeaders(httpWrapper)
    assertEquals(2,httpWrapper.headers.size)
    assertEquals("text/plain",httpWrapper.getHeader("content-type"))
  }

  @Test
  def testActor() : Unit = {
    implicit val system = ActorSystem()
    val probe = TestProbe()
    //probe.expectMsg(30 seconds, classOf[WebParsedRequestMessage])
    val actor = system.actorOf(Props(new SendBackActor("abc") {
      override def sendBack(msg: WebParsedResponseMessage): Unit = {
        probe.ref ! msg
      }
      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc","next")
      }
    }))
    val message = new WebParsedResponseMessage(createWrapper(),null,null,null,null)
    message.meta.put("__process_start",new Metric().time().toLong)
    message.meta.put("__start",new Metric().time().toLong)
    actor ! message
    val response = probe.expectMsgClass(classOf[WebParsedResponseMessage])
    assertEquals(200,response.response.status)
    Thread.sleep(500)
    system.terminate()
  }
}
