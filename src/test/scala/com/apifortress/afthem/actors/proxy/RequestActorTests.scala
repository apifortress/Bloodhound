package com.apifortress.afthem.actors.proxy

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.apifortress.afthem.config.{Backend, Phase}
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage, WebRawRequestMessage}
import javax.servlet.http.HttpServletRequest
import org.junit.Assert._
import org.junit.Test
import org.mockito.Mockito._

class RequestActorTests {

  @Test
  def testActor() : Unit = {
    val headers = Map[String,String]("accept"->"application/json")
    val iterator = headers.iterator
    val headerNames = new java.util.Enumeration[String]{
      override def hasMoreElements() : Boolean = iterator.hasNext

      override def nextElement(): String = iterator.next()._1
    }
    val rawRequest = mock(classOf[HttpServletRequest])
    when(rawRequest.getHeaderNames).thenReturn(headerNames)
    when(rawRequest.getHeader("accept")).thenReturn("application/json")
    when(rawRequest.getRequestURL).thenReturn(new StringBuffer("http://foo.com"))
    when(rawRequest.getMethod).thenReturn("GET")
    val backend = new Backend("123","123",null,null)
    val request = new WebRawRequestMessage(rawRequest, backend,null,null)

    implicit val system = ActorSystem()
    val probe = TestProbe()
    val actor = system.actorOf(Props(new RequestActor("abc") {
      override def forward(msg: BaseMessage): Unit = {
        probe.ref ! msg
      }

      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc","next")
      }
    }))
    actor ! request
    val response = probe.expectMsgClass(classOf[WebParsedRequestMessage])
    assertEquals("application/json",response.request.getHeader("accept"))
    Thread.sleep(500)
    system.terminate()
  }
}
