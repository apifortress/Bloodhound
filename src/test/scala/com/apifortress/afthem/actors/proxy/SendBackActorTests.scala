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
