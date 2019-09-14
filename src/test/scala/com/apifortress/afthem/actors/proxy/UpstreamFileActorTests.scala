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
