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
package com.apifortress.afthem.actors.sidecars.serializers

import com.apifortress.afthem.TestData
import com.apifortress.afthem.messages.WebParsedResponseMessage
import org.junit.Test
import org.junit.Assert._
class AbstractSerializerTests {

  @Test
  def testShouldCapture() : Unit = {
    val responseWrapper = TestData.createWrapper()
    responseWrapper.setHeader("content-type","application/json")
    val response = new WebParsedResponseMessage(responseWrapper, responseWrapper,null,null,null)
    assertTrue(AbstractSerializerActor.shouldCapture(response,"x-key",null,List("json")))
    assertFalse(AbstractSerializerActor.shouldCapture(response,"x-jay",null,List("json")))
    assertFalse(AbstractSerializerActor.shouldCapture(response,"x-key",null,List("text")))
    assertTrue(AbstractSerializerActor.shouldCapture(response,null,"foo",List("json")))
    assertFalse(AbstractSerializerActor.shouldCapture(response,null,"x-key",List("json")))
  }
}
