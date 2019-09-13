package com.apifortress.afthem.config

import org.junit.Test
import org.junit.Assert._
class ApiKeysTests {


  @Test
  def testApiKey() : Unit = {
    val keys = new ApiKeys(List(new ApiKey("123","abc",true)))
    assertEquals("abc",keys.getApiKey("123").get.appId)
  }

  @Test
  def testNoApiKey() : Unit = {
    val keys = new ApiKeys(List(new ApiKey("123","abc",true)))
    assertFalse(keys.getApiKey("111").isDefined)
  }

  @Test
  def testNoKeyProvided() : Unit = {
    val keys = new ApiKeys(List(new ApiKey("123","abc",true)))
    assertFalse(keys.getApiKey(null).isDefined)
  }
}
