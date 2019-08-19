package com.apifortress.afthem.messages.beans

import org.junit.Test
import org.junit.Assert._

class HttpWrapperTests {



  @Test
  def testUriComponents() = {
    val wrapper = new HttpWrapper("http://example.com/foobar?a=b")
    assertNotNull(wrapper.uriComponents)
    assertEquals("example.com",wrapper.uriComponents.getHost)
    assertEquals(-1,wrapper.uriComponents.getPort)
    assertTrue(wrapper.uriComponents.getQueryParams.containsKey("a"))
  }

  @Test
  def testGetHeader() = {
    val wrapper = new HttpWrapper("http://example.com/foobar?a=b",
                              -1,"GET",
                                      List(new Header("content-type","application/json"),
                                            new Header("accept","text/plain")))
    assertEquals("application/json",wrapper.getHeader("content-type"))
    assertNull(wrapper.getHeader("banana"))
    assertNotNull(wrapper.getHeader("content-type"))
  }

  @Test
  def testSetHeader() = {
    val wrapper = new HttpWrapper("http://example.com/foobar?a=b")
    wrapper.setHeader("foo", "bar")
    assertEquals("bar", wrapper.getHeader("foo"))
  }

  @Test
  def testRemoveHeader() = {
    val wrapper = new HttpWrapper("http://example.com/foobar?a=b",
      -1,"GET",
      List(new Header("content-type","application/json"),
        new Header("accept","text/plain"),
        new Header("via","Afthem")))
    wrapper.removeHeaders(List("via","accept"))
    assertEquals(1,wrapper.headers.size)
    wrapper.removeHeader("content-type")
    assertEquals(0,wrapper.headers.size)
  }
}
