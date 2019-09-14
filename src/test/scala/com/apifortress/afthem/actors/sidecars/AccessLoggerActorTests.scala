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
import akka.testkit.TestProbe
import com.apifortress.afthem.TestData
import com.apifortress.afthem.exceptions.{RejectedRequestException, UnauthorizedException}
import com.apifortress.afthem.messages.{ExceptionMessage, WebParsedRequestMessage, WebParsedResponseMessage}
import org.junit.Test

class AccessLoggerActorTests {

  @Test
  def testActor() : Unit = {
    implicit val system = ActorSystem()
    val start = System.currentTimeMillis()
    val actor = system.actorOf(Props(new AccessLoggerActor("abc")))
    val requestMessage = new WebParsedRequestMessage(TestData.createWrapper(),null,null,null)
    val responseMessage = new WebParsedResponseMessage(TestData.createWrapper(), TestData.createWrapper(),null,null,null)
    actor ! requestMessage
    actor ! responseMessage
    actor ! new ExceptionMessage(new RejectedRequestException(requestMessage),500,requestMessage)
    actor ! new ExceptionMessage(new UnauthorizedException(requestMessage),500,requestMessage)
    Thread.sleep(500)
    system.terminate()
  }
}
