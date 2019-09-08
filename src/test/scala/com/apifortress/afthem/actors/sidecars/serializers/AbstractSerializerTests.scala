package com.apifortress.afthem.actors.sidecars.serializers

import com.apifortress.afthem.TestData
import com.apifortress.afthem.messages.WebParsedResponseMessage
import org.junit.Test
import org.junit.Assert._
class AbstractSerializerTests {

  @Test
  def testShouldCapture() : Unit = {
    val responseWrapper = TestData.createWrapper()
    responseWrapper.setHeader("content-type","application/json")
    val response = new WebParsedResponseMessage(responseWrapper, responseWrapper,null,null,null)
    assertTrue(AbstractSerializerActor.shouldCapture(response,"x-key",null,List("json")))
    assertFalse(AbstractSerializerActor.shouldCapture(response,"x-jay",null,List("json")))
    assertFalse(AbstractSerializerActor.shouldCapture(response,"x-key",null,List("text")))
    assertTrue(AbstractSerializerActor.shouldCapture(response,null,"foo",List("json")))
    assertFalse(AbstractSerializerActor.shouldCapture(response,null,"x-key",List("json")))
  }
}
