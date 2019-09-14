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
package com.apifortress.afthem.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.apifortress.afthem.TestData
import com.apifortress.afthem.actors.proxy.RequestActor
import com.apifortress.afthem.config.loaders.YamlConfigLoader
import com.apifortress.afthem.config.{Backend, Flows, Phase}
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage, WebRawRequestMessage}
import javax.servlet.http.HttpServletRequest
import org.junit.Test
import org.junit.Assert._
import org.mockito.Mockito.{mock, when}

class AbstractAfthemActorTests {

  @Test
  def testSelectNextActor() : Unit = {
    val flow = Flows.instance().getFlow("default")
    val request = new WebParsedRequestMessage(TestData.createWrapper(),null,flow,null)
    val nextActorSelector = AbstractAfthemActor.selectNextActor(request,"proxy/request")
    assertEquals("/user/proxy/upstream_http",nextActorSelector.pathString)
  }

  @Test
  def testAbstractActor() : Unit = {
    YamlConfigLoader.SUBPATH = "etc.test"
    val rawRequest = mock(classOf[HttpServletRequest])
    val backend = new Backend("123","123",null,null)
    val request = new WebRawRequestMessage(rawRequest, backend,Flows.instance().getFlow("default"),null)
    implicit val system = ActorSystem()
    val probe = TestProbe()
    val actor = system.actorOf(Props(new RequestActor("proxy/request") {
      override def receive = {
        case x: WebRawRequestMessage =>
          assertEquals("proxy/request",getPhaseId())
          assertEquals("proxy/request",getPhase(x).id)
      }

    }))
    actor ! request
    Thread.sleep(500)
    system.terminate()
  }
}
