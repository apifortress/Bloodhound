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
