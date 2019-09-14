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
package com.apifortress.afthem.exceptions

import com.apifortress.afthem.messages.WebParsedRequestMessage
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito._

class ExceptionsTests {

  @Test
  def testBackendConfigurationMissingException : Unit = {
    assertEquals("No backend configuration found",new BackendConfigurationMissingException().getMessage)
  }

  @Test
  def testGenericException : Unit = {
    assertEquals("Generic exception during : foobar",new GenericException("foobar").getMessage)
  }

  @Test
  def testRejectedRequestException : Unit = {
    val message = mock(classOf[WebParsedRequestMessage])
    assertEquals("Request has been rejected",new RejectedRequestException(message).getMessage)
  }

  @Test
  def testUnauthorizedException : Unit = {
    val message = mock(classOf[WebParsedRequestMessage])
    assertEquals("API Key not authorized",new UnauthorizedException(message).getMessage)
  }

  @Test
  def testAfhtemFlowException : Unit = {
    val message = mock(classOf[WebParsedRequestMessage])
    assertEquals("foobar",new AfthemFlowException(message,"foobar").getMessage)
  }
}
