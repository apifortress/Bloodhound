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
package com.apifortress.afthem.actors.sidecars

import akka.actor.{ActorSystem, Props}
import com.apifortress.afthem.TestData
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage}
import org.junit.Test

class GenericLoggerActorTests {

  @Test
  def testActor() : Unit = {
    implicit val system = ActorSystem()
    val start = System.currentTimeMillis()
    val actor = system.actorOf(Props(new GenericLoggerActor("abc"){
      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc","next",List.empty[String],Map("value"->"#msg.request()", "evaluated"->true))
      }
    }))
    val requestMessage = new WebParsedRequestMessage(TestData.createWrapper(),null,null,null)
    actor ! requestMessage
    Thread.sleep(500)
    system.terminate()
  }
}
