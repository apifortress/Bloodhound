package com.apifortress.afthem.actors.transformers

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.apifortress.afthem.TestData
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage}
import org.junit.Assert._
import org.junit.Test

class ReplaceUpstreamActorTests {

  @Test
  def testActorPositive() : Unit = {
    implicit val system = ActorSystem()
    val probe = TestProbe()
    val actor = system.actorOf(Props(new ReplaceUpstreamActor("abc") {
      override def forward(msg: BaseMessage): Unit = {
        probe.ref ! msg
      }

      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc", "next",List.empty[String],Map("expression"->"#msg.request().getHeader('x-key')!=null",
                                                                    "upstream"->"http://example.com"))
      }
    }))

    val message = new WebParsedRequestMessage(TestData.createWrapper(),null,null,null)
    actor ! message
    val request = probe.expectMsgClass(classOf[WebParsedRequestMessage])
    assertEquals("http://example.com",request.meta.get("__replace_upstream").get)
    Thread.sleep(500)
    system.terminate()
  }

  @Test
  def testActorNegative() : Unit = {
    implicit val system = ActorSystem()
    val probe = TestProbe()
    val actor = system.actorOf(Props(new ReplaceUpstreamActor("abc") {
      override def forward(msg: BaseMessage): Unit = {
        probe.ref ! msg
      }

      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc", "next",List.empty[String],Map("expression"->"#msg.request().getHeader('x-banana')!=null",
          "upstream"->"http://example.com"))
      }
    }))

    val message = new WebParsedRequestMessage(TestData.createWrapper(),null,null,null)
    actor ! message
    val request = probe.expectMsgClass(classOf[WebParsedRequestMessage])
    assertFalse(request.meta.get("__replace_upstream").isDefined)
    Thread.sleep(500)
    system.terminate()
  }
}
