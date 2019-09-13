package com.apifortress.afthem.actors.proxy

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.apifortress.afthem.{Metric, TestData}
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.messages.{BaseMessage, WebParsedResponseMessage}
import org.junit.Assert._
import org.junit.Test

class SendBackActorTests {


  @Test
  def testAdjustResponseHeaders() : Unit = {
    val httpWrapper = TestData.createWrapper()

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
    val actor = system.actorOf(Props(new SendBackActor("abc") {
      override def sendBack(msg: WebParsedResponseMessage): Unit = {
        probe.ref ! msg
      }
      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc","next")
      }
    }))
    val message = new WebParsedResponseMessage(TestData.createWrapper(),null,null,null,null)
    message.meta.put("__process_start",new Metric().time().toLong)
    message.meta.put("__start",new Metric().time().toLong)
    actor ! message
    val response = probe.expectMsgClass(classOf[WebParsedResponseMessage])
    assertEquals(200,response.response.status)
    Thread.sleep(500)
    system.terminate()
  }
}
