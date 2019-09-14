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
package com.apifortress.afthem.actors.sidecars.serializers

import java.io.{File, FileInputStream}

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.apifortress.afthem.{Parsers, TestData}
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.messages.{BaseMessage, WebParsedResponseMessage}
import org.apache.commons.io.IOUtils
import org.junit.Test
import org.junit.Assert._

import scala.collection.mutable

class FileAppenderSerializerActorTests {

  @Test
  def testActor() : Unit = {
    val wrapper = TestData.createWrapper()
    wrapper.setHeader("content-type","application/json")
    wrapper.payload = "{\"foo\":\"bar\"}".getBytes
    val message = new WebParsedResponseMessage(wrapper,wrapper,null,null,null)
    implicit val system = ActorSystem()
    val probe = TestProbe()
    //probe.expectMsg(30 seconds, classOf[WebParsedRequestMessage])
    val file = File.createTempFile("afthem","tests")
    val actor = system.actorOf(Props(new FileAppenderSerializerActor("abc") {
      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc","next",null, Map("filename"->file.getAbsolutePath))
      }
    }))
    actor ! message
    Thread.sleep(500)
    system.stop(actor)
    Thread.sleep(100)
    val output = Parsers.parseJSON(IOUtils.toString(new FileInputStream(file),"UTF-8"),classOf[mutable.Map[String,Any]])
    val expected = Parsers.parseJSON("{\"request\":{\"querystring\":{},\"request_uri\":\"http://foo.com\",\"uri\":\"\",\"method\":\"GET\",\"size\":13,\"body\":\"{\\\"foo\\\":\\\"bar\\\"}\",\"headers\":{\"content-encoding\":\"gzip\",\"content-type\":\"application/json\",\"x-key\":\"ABC123\",\"content-length\":\"123\"}},\"download_time\":null,\"started_at\":1567958859486,\"response\":{\"size\":13,\"body\":\"{\\\"foo\\\":\\\"bar\\\"}\",\"status\":200,\"headers\":{\"content-encoding\":\"gzip\",\"content-type\":\"application/json\",\"x-key\":\"ABC123\",\"content-length\":\"123\"}},\"client_ip\":null}",classOf[mutable.Map[String,Any]])
    output.remove("started_at")
    expected.remove("started_at")
    assertEquals(expected,output)

  }
}
