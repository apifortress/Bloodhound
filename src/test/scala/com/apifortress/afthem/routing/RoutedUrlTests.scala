package com.apifortress.afthem.routing

import com.apifortress.afthem.config.Probe
import org.junit.Test
import org.junit.Assert._

class RoutedUrlTests {


  @Test
  def testRoutedUrlStates() : Unit = {
    val probe = new Probe("/foo",200,"GET","5 seconds","10 seconds",2,3)
    val routedUrl = new RoutedUrl("http://example.com",probe)
    assertEquals("http://example.com",routedUrl.url)
    assertEquals(true,routedUrl.upStatus)
    routedUrl.addStatus(false)
    assertEquals(true,routedUrl.upStatus)
    routedUrl.addStatus(false)
    assertEquals(false,routedUrl.upStatus)
    routedUrl.addStatus(true)
    assertEquals(false,routedUrl.upStatus)
    routedUrl.addStatus(true)
    assertEquals(false,routedUrl.upStatus)
    routedUrl.addStatus(true)
    assertEquals(true,routedUrl.upStatus)
  }

}
