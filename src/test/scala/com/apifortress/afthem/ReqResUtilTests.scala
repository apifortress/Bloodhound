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
import javax.servlet.http.HttpServletRequest
import org.junit.Assert._
import org.junit.Test
import org.mockito.Mockito._

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
                          "GET", List.empty[Header],data,null,ReqResUtil.CHARSET_UTF8)
    assertEquals("Foobar",ReqResUtil.byteArrayToString(wrapper))
  }

  @Test
  def testParseHeaders() : Unit = {

    val headers = Map[String,String]("content-type"->"application/json")
    val iterator = headers.iterator
    val headerNames = new java.util.Enumeration[String]{
      override def hasMoreElements() : Boolean = iterator.hasNext

      override def nextElement(): String = iterator.next()._1
    }
    val request = mock(classOf[HttpServletRequest])
    when(request.getHeaderNames).thenReturn(headerNames)
    when(request.getHeader("content-type")).thenReturn("application/json")
    val res = ReqResUtil.parseHeaders(request)
    assertEquals(1,res._1.count(it => it.key == "content-type"))
    assertEquals(1,res._2.count(it => it._1 == "content-type"))
  }

  @Test
  def testExtractAccept() : Unit = {
    val request = mock(classOf[HttpServletRequest])
    when(request.getHeader("accept")).thenReturn("text/plain")
    assertEquals("text/plain",ReqResUtil.extractAccept(request))
    when(request.getHeader("accept")).thenReturn(null)
    assertEquals("application/json",ReqResUtil.extractAccept(request))
  }

  @Test
  def testAcceptFromMessage() : Unit = {
    val request = TestData.createWrapper()
    request.setHeader("accept","text/plain")
    assertEquals("text/plain",ReqResUtil.extractAcceptFromMessage(new WebParsedRequestMessage(request,null,null,null)))
    assertEquals("text/plain",ReqResUtil.extractAcceptFromMessage(new WebParsedResponseMessage(null,request, null,null,null)))
  }

}
