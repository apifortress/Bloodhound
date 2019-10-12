/**
  * Copyright 2019 API Fortress
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  * @author Simone Pezzano
  */
package com.apifortress.afthem.actors.filters

import java.io.{File, FileWriter}

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.apifortress.afthem.{AfthemResult, TestData}
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.config.loaders.YamlConfigLoader
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
