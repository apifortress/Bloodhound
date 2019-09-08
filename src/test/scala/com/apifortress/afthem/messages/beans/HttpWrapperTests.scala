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
    wrapper.setHeader("foo","dog")
    assertEquals("dog", wrapper.getHeader("foo"))
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

  @Test
  def testSetUrl() : Unit = {
    val wrapper = new HttpWrapper("http://example.com/foobar?a=b")
    wrapper.setURL("http://foo.com")
    assertEquals("foo.com",wrapper.uriComponents.getHost)
  }

  @Test
  def testClone() : Unit = {
    val wrapper = new HttpWrapper("http://example.com/foobar?a=b")
    val wrapper2 = wrapper.clone()
    wrapper2.setURL("http://foo.com")
    assertNotEquals(wrapper.getURL(),wrapper2.getURL())
  }
}
