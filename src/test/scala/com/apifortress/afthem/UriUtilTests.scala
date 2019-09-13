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

import com.apifortress.afthem.config.Backend
import org.junit.Test
import org.junit.Assert._

class UriUtilTests {

  @Test
  def testGetSignature() = {
    assertEquals("example.com/foobar",UriUtil.getSignature("http://example.com/foobar?a=b"))
    assertEquals("example.com/foobar",UriUtil.getSignature("http://example.com:8080/foobar?a=b"))
  }

  @Test
  def testDetermineUpstreamPartStringPrefix() = {
    val uri1 = UriUtil.toUriComponents("http://example.com/foo/bar?a=b")
    val backend = new Backend(null,"example.com/foo", null, null)
    assertEquals("/bar?a=b",UriUtil.determineUpstreamPart(uri1,backend))

    val uri2 = UriUtil.toUriComponents("http://example.com:8080/foo/bar?a=b")
    assertEquals("/bar?a=b",UriUtil.determineUpstreamPart(uri1,backend))
  }

  @Test
  def testDetermineUpstreamPartRegexPrefix() = {
    val uri1 = UriUtil.toUriComponents("http://example.com/foo/bar?a=b")
    val backend = new Backend(null,"[^/]*/foo", null, null)
    assertEquals("/bar?a=b",UriUtil.determineUpstreamPart(uri1,backend))

    val uri2 = UriUtil.toUriComponents("http://example.com:8080/foo/bar?a=b")
    assertEquals("/bar?a=b",UriUtil.determineUpstreamPart(uri1,backend))
  }

  @Test
  def testComposeUriAndQuery() : Unit = {
    assertEquals("http://foo.com/bar",UriUtil.composeUriAndQuery("http://foo.com/bar",null))
    assertEquals("http://foo.com/bar",UriUtil.composeUriAndQuery("http://foo.com/bar", ""))
    assertEquals("http://foo.com/bar?foo=bar",
                  UriUtil.composeUriAndQuery("http://foo.com/bar","foo=bar"))
  }
}
