 /*
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

import com.apifortress.afthem.messages.{WebParsedRequestMessage, WebParsedResponseMessage}
import com.apifortress.afthem.messages.beans.{Header, HttpWrapper}
import org.junit.Test
import org.junit.Assert._

class ReqResUtilTests {

  @Test
  def testExtractAcceptFromMessage() ={
    val request = new WebParsedRequestMessage(new HttpWrapper("http://example.com",-1,"GET",
                                                List(new Header("accept","text/xml"))),
                                            null,null,null)
    val accept = ReqResUtil.extractAcceptFromMessage(request)
    assertEquals("text/xml",accept)
  }

  @Test
  def testExtractContentType() = {
    val wrapper = new HttpWrapper("http://example.com",-1,"GET",
                                    List(new Header("content-type","text/xml")))
    assertEquals("text/xml",ReqResUtil.extractContentType(wrapper))
  }

  @Test
  def testDetermineMimeFromContentType() = {
    assertEquals("application/json",ReqResUtil.determineMimeFromContentType("application/json"))
    assertEquals("application/json",ReqResUtil.determineMimeFromContentType("application/hal+json"))
    assertEquals("text/xml",ReqResUtil.determineMimeFromContentType("application/xml"))
    assertEquals("text/plain",ReqResUtil.determineMimeFromContentType("application/foobar"))
  }

  @Test
  def testByteArrayToString() = {
    val data = "Foobar".getBytes
    val wrapper = new HttpWrapper("http://example.com",-1,
                          "GET", List.empty[Header],data,null,"UTF-8")
    assertEquals("Foobar",ReqResUtil.byteArrayToString(wrapper))
  }

}
