package com.apifortress.afthem.actors.proxy

import com.apifortress.afthem.ReqResUtil
import com.apifortress.afthem.messages.beans.{Header, HttpWrapper}
import org.junit.Assert._
import org.junit.Test

class SendBackActorTests {

  @Test
  def testAdjustResponseHeaders() : Unit = {
    val httpWrapper = new HttpWrapper("http://foo.com",200,null,
                                        List[Header](new Header(ReqResUtil.HEADER_CONTENT_LENGTH,"123"),
                                          new Header(ReqResUtil.HEADER_CONTENT_ENCODING,"gzip"),
                                          new Header("x-key","ABC123")))

    SendBackActor.adjustResponseHeaders(httpWrapper)
    assertEquals(2,httpWrapper.headers.size)
    assertEquals("application/octet-stream",httpWrapper.getHeader("content-type"))

    val httpWrapper2 = new HttpWrapper("http://foo.com",200,null,
                                          List[Header](new Header(ReqResUtil.HEADER_CONTENT_LENGTH,"123"),
                                            new Header(ReqResUtil.HEADER_CONTENT_ENCODING,"gzip"),
                                            new Header("content-type","text/plain")))
    SendBackActor.adjustResponseHeaders(httpWrapper2)
    assertEquals(1,httpWrapper2.headers.size)
    assertEquals("text/plain",httpWrapper2.getHeader("content-type"))

  }
}
