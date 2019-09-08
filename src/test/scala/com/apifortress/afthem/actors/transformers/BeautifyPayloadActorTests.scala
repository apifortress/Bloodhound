package com.apifortress.afthem.actors.transformers

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.apifortress.afthem.TestData
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage, WebParsedResponseMessage}
import org.junit.Assert._
import org.junit.Test

import scala.concurrent.duration._


class BeautifyPayloadActorTests {

  @Test
  def testBeautificationJson() : Unit = {
    val data = "{\"foo\":\"bar\"}"
    val result = BeautifyPayloadActor.beautify(data.getBytes,"json",null)
    assertEquals("{\n  \"foo\" : \"bar\"\n}",new String(result))
  }

  @Test
  def testBeautificationXml() : Unit = {
    val data = "<root attr=\"attr1\"><foo>bar</foo></root>"
    val result = BeautifyPayloadActor.beautify(data.getBytes,"xml",null)
    assertEquals("<root attr=\"attr1\">\n    <foo>bar</foo>\n</root>", new String(result))
  }

  @Test
  def testActor() : Unit = {
    implicit val system = ActorSystem()
    val probe = TestProbe()
    val actor = system.actorOf(Props(new BeautifyPayloadActor("abc") {
      override def forward(msg: BaseMessage): Unit = {
        probe.ref ! msg
      }

      override def getPhase(message: BaseMessage): Phase = {
        return new Phase("abc","next")
      }
    }))
    val wrapper = TestData.createWrapper()
    wrapper.payload = "{\"foo\":\":bar\"}".getBytes
    actor ! new WebParsedResponseMessage(wrapper,null,null,null,null)
    val response = probe.expectMsgClass(5 seconds, classOf[WebParsedResponseMessage])
    assertEquals("{\n  \"foo\" : \":bar\"\n}",new String(response.response.payload))

    actor ! new WebParsedRequestMessage(wrapper,null,null,null)
    val response2 = probe.expectMsgClass(5 seconds, classOf[WebParsedRequestMessage])
    assertEquals("{\n  \"foo\" : \":bar\"\n}",new String(response2.request.payload))
    Thread.sleep(500)
    system.terminate()
  }
}
