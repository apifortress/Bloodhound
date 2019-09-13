package com.apifortress.afthem.actors.proxy

import java.io.{File, FileWriter}

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.apifortress.afthem.Metric
import com.apifortress.afthem.config.{Backend, Phase}
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage, WebParsedResponseMessage}
import com.apifortress.afthem.messages.beans.HttpWrapper
import org.apache.commons.io.IOUtils
import org.junit.Test
import org.junit.Assert._

class UpstreamFileActorTests {


  @Test
  def testActor() : Unit = {

    val file = File.createTempFile("afthem","test")
    val writer = new FileWriter(file)
    IOUtils.write("{\"foo\":\"bar\"}",writer)
    writer.close()
    implicit val system = ActorSystem()
    val probe = TestProbe()
    val actor = system.actorOf(Props(new UpstreamFileActor("abc") {
      override def forward(msg: BaseMessage): Unit = {
        probe.ref ! msg
      }

      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc","next", List.empty[String],Map("basepath"->file.getParentFile.getAbsolutePath))
      }
    }))
    val request = new HttpWrapper("https://foo.com/"+file.getName,-1,"GET")
    val message = new WebParsedRequestMessage(request,new Backend("foo", "foo.com",null,null),null,null)
    message.meta.put("__process_start",new Metric().time().toLong)
    message.meta.put("__start",new Metric().time().toLong)
    actor ! message
    val response = probe.expectMsgClass(classOf[WebParsedResponseMessage])
    assertEquals("{\"foo\":\"bar\"}",new String(response.response.payload))
    Thread.sleep(500)
    system.terminate()
  }
}
