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
