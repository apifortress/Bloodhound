package com.apifortress.afthem.actors.filters

import java.io.{File, FileWriter}

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.apifortress.afthem.TestData
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.config.loaders.YamlConfigLoader
import com.apifortress.afthem.messages.beans.AfthemResult
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage}
import org.apache.commons.io.IOUtils
import org.junit.Assert.assertEquals
import org.junit.{Before, Test}


class ApiKeyFilterActorTests {


  var path : String = null

  @Before
  def initFile() : Unit = {
    YamlConfigLoader.SUBPATH="etc.test"
    val file = File.createTempFile("afthem","test")
    val fileWriter = new FileWriter(file)
    IOUtils.write("api_keys:\n- api_key: ABC123\n  app_id: John Doe\n  enabled: true",fileWriter)
    fileWriter.close()
    path = file.getAbsolutePath
  }

  @Test
  def testActorWithHeader() : Unit = {

    val result = new AfthemResult(){
      override def setData(exception: Exception, status: Int, contentType: String, message: BaseMessage): Unit = {
        assertEquals(401,status)
      }
    }

    implicit val system = ActorSystem()
    val probe = TestProbe()
    val actor = system.actorOf(Props(new ApiKeyFilterActor("abc") {
      override def tellNextActor(message: BaseMessage) : Unit = {
        probe.ref ! message
      }
      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc","next",List.empty[String],Map("in"->"header","name"->"x-key","filename"->path))
      }
    }))
    val message = new WebParsedRequestMessage(TestData.createWrapper(),null,null,result)

    actor ! message
    val request = probe.expectMsgClass(classOf[WebParsedRequestMessage])
    assertEquals("ABC123",request.request.getHeader("x-key"))
    message.request.setHeader("x-key","111")
    actor ! message
    Thread.sleep(500)
    system.terminate()
  }

  @Test
  def testActorWithQuery() : Unit = {
    val result = new AfthemResult(){
      override def setData(exception: Exception, status: Int, contentType: String, message: BaseMessage): Unit = {
        assertEquals(401,status)
      }
    }

    implicit val system = ActorSystem()
    val probe = TestProbe()
    val actor = system.actorOf(Props(new ApiKeyFilterActor("abc") {
      override def tellNextActor(message: BaseMessage) : Unit = {
        probe.ref ! message
      }
      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc","next",List.empty[String],Map("in"->"query","name"->"x-key","filename"->path))
      }
    }))
    val wrapper = TestData.createWrapper()
    wrapper.setURL("http://foo.com/bar?x-key=ABC123")
    val message = new WebParsedRequestMessage(wrapper,null,null,result)

    actor ! message
    val request = probe.expectMsgClass(classOf[WebParsedRequestMessage])
    assertEquals("ABC123",request.request.getHeader("x-key"))
    message.request.setHeader("x-key","111")
    actor ! message
    Thread.sleep(500)
    system.terminate()
  }
}
