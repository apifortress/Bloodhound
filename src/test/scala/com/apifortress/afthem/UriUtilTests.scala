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
  def testDetermineUpstreamPart() = {
    val uri1 = UriUtil.toUriComponents("http://example.com/foo/bar?a=b")
    val backend = new Backend(null,"http://example.com/foo", null, null)
    assertEquals("/bar?a=b",UriUtil.determineUpstreamPart(uri1,backend))

    val uri2 = UriUtil.toUriComponents("http://example.com:8080/foo/bar?a=b")
    assertEquals("/bar?a=b",UriUtil.determineUpstreamPart(uri1,backend))
  }
}
