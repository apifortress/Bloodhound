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
import org.junit.Test
import org.junit.Assert._

class DeserializerActorTests {

  @Test
  def testDeserializeJson() : Unit = {
    val data = "{\"foo\":\"bar\"}"
    val deserialized = DeserializerActor.deserialize(data,"application/json")
    assertEquals("bar",deserialized.asInstanceOf[Map[String,String]].get("foo").get)

    val deserialized2 = DeserializerActor.deserialize(data.getBytes,"application/json")
    assertEquals("bar",deserialized2.asInstanceOf[Map[String,String]].get("foo").get)
  }

  @Test
  def testDeserializeXml() : Unit = {
    val data = "<root><foo>bar</foo></root>"
    val deserialized = DeserializerActor.deserialize(data,"text/xml")
    assertEquals("bar",deserialized.asInstanceOf[Map[String,String]].get("foo").get)

    val deserialized2 = DeserializerActor.deserialize(data.getBytes,"text/xml")
    assertEquals("bar",deserialized2.asInstanceOf[Map[String,String]].get("foo").get)
  }

  @Test
  def testActor() : Unit = {
    implicit val system = ActorSystem()
    val probe = TestProbe()
    val actor = system.actorOf(Props(new DeserializerActor("abc") {
      override def tellNextActor(message: BaseMessage) : Unit = {
        probe.ref ! message
      }
      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc","next",List.empty[String],Map("expression"->"#msg.request().payload()","meta"->"foo","contentType"->"json"))
      }
    }))
    val message = new WebParsedRequestMessage(TestData.createWrapper(),null,null,null)
    message.request.payload = "{\"foo\":\"bar\"}".getBytes
    actor ! message
    val request = probe.expectMsgClass(classOf[WebParsedRequestMessage])
    assertEquals("bar",request.meta.get("foo").get.asInstanceOf[Map[String,String]].get("foo").get)
    Thread.sleep(500)
    system.terminate()
  }

}
