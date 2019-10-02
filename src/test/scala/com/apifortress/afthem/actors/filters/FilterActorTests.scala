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

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.apifortress.afthem.TestData
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.messages.beans.AfthemResult
import com.apifortress.afthem.messages.{BaseMessage, ExceptionMessage, WebParsedRequestMessage}
import org.junit.Assert.assertEquals
import org.junit.Test

import scala.concurrent.duration._

class FilterActorTests {

  @Test
  def testActor() : Unit = {
    val result = new AfthemResult(){
      override def setData(exception: Exception, status: Int, contentType: String, message: BaseMessage): Unit = {
        assertEquals(400,status)
      }
    }

    implicit val system = ActorSystem()
    val probe = TestProbe()
    val actor = system.actorOf(Props(new FilterActor("abc") {
      override def tellNextActor(message: BaseMessage) : Unit = {
        probe.ref ! message
      }
      override def tellSidecars(message: BaseMessage) : Unit = {
        probe.ref ! message
      }
      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc","next",List.empty[String],Map("accept"->
                                                                  List(Map("evaluated"->true,
                                                                        "value"->"#msg.request().getHeader('x-key')!=null")),
                                                                  "reject"->
                                                                   List(Map("evaluated"->true,
                                                                          "value"->"#msg.request().getHeader('banana')!=null"))))
      }
    }))
    val message = new WebParsedRequestMessage(TestData.createWrapper(),null,null,result)
    actor ! message
    val request = probe.expectMsgClass(classOf[WebParsedRequestMessage])
    assertEquals("ABC123",request.request.getHeader("x-key"))
    Thread.sleep(500)

    val wrapper = TestData.createWrapper()
    wrapper.setHeader("banana","123")
    val message2 = new WebParsedRequestMessage(wrapper,null,null,result)
    actor ! message2
    val request2 = probe.expectMsgClass(5 seconds,classOf[ExceptionMessage])
    system.terminate()
  }
}
