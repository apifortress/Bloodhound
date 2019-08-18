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

  def testExtractHost() = {
    assertEquals("www.google.com",ReqResUtil.extractHost("http://www.google.com"))
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
