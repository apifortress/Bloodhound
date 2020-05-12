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
package com.apifortress.afthem

import java.io.IOException

import org.junit.Test
import org.junit.Assert._

class ResponseEntityUtilTests {

  @Test
  def testCreateEntity() :  Unit = {
    val wrapper = TestData.createWrapper()
    wrapper.payload = "{\"foo\":\"bar\"}".getBytes
    val entity = ResponseEntityUtil.createEntity(wrapper)
    assertEquals(200,entity.getStatusCodeValue)
    assertEquals(123,entity.getHeaders.getContentLength)
  }

  @Test
  def testCreateEntityForException() : Unit = {
    var entity = ResponseEntityUtil.createEntity(new IOException("foobar"),500)
    assertTrue(entity.getStatusCode.is5xxServerError())
    println(new String(entity.getBody))
    assertEquals("{\n  \"status\" : \"error\",\n  \"message\" : \"IOException: foobar\"\n}",new String(entity.getBody))

    entity = ResponseEntityUtil.createEntity(new IOException("foobar"),500,"text/xml")
    assertEquals("<exception><status>error</status><message>IOException: foobar</message></exception>",
                  new String(entity.getBody))

    entity = ResponseEntityUtil.createEntity(new IOException("foobar"),500,"text/plain")
    assertEquals("status: error\nexception: IOException: foobar", new String(entity.getBody))
  }
}
