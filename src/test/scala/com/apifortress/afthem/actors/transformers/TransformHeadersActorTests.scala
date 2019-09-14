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
package com.apifortress.afthem.actors.transformers

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.apifortress.afthem.TestData
import com.apifortress.afthem.actors.filters.FilterActor
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage, WebParsedResponseMessage}
import com.apifortress.afthem.messages.beans.Header
import org.junit.Test
import org.junit.Assert._

class TransformHeadersActorTests {

  @Test
  def testModifyHeaders() : Unit = {
    val wrapper = TestData.createWrapper()

    TransformHeadersActor.removeHeaders(wrapper,List(new Header("foobar",null)))

    TransformHeadersActor.removeHeaders(wrapper,List(new Header("x-key",null)))
    assertFalse(wrapper.headers.exists(it => it.key=="x-key"))

    TransformHeadersActor.addHeaders(wrapper,List(new Header("x-key","abc"),new Header("x-day","abc")))
    assertEquals("abc",wrapper.getHeader("x-key"))

    TransformHeadersActor.setHeaders(wrapper,List(new Header("x-key","123")))
    assertEquals("123",wrapper.getHeader("x-key"))
  }

  @Test
  def testActor() : Unit = {
    implicit val system = ActorSystem()
    val probe = TestProbe()
    val actor = system.actorOf(Props(new TransformHeadersActor("abc") {
      override def tellNextActor(message: BaseMessage) : Unit = {
        probe.ref ! message
      }
      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc","next",List.empty[String],Map("remove"->List(Map("name"->"x-key")),"add"->List(Map("name"->"x-foo","value"->"bar"))))
      }
    }))
    val message = new WebParsedRequestMessage(TestData.createWrapper(),null,null,null)
    actor ! message
    val request = probe.expectMsgClass(classOf[WebParsedRequestMessage])
    assertTrue(request.request.getHeader("x-key")==null)
    assertEquals("bar",request.request.getHeader("x-foo"))

    val message2 = new WebParsedResponseMessage(TestData.createWrapper(),null,null,null,null)
    actor ! message2
    val response = probe.expectMsgClass(classOf[WebParsedResponseMessage])
    assertTrue(response.response.getHeader("x-key")==null)
    assertEquals("bar",response.response.getHeader("x-foo"))
    Thread.sleep(500)
    system.terminate()
  }
}
