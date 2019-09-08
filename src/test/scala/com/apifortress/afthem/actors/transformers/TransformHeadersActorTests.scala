package com.apifortress.afthem.actors.transformers

import com.apifortress.afthem.TestData
import com.apifortress.afthem.messages.beans.Header
import org.junit.Test
import org.junit.Assert._

class TransformHeadersActorTests {

  @Test
  def testModifyHeaders() : Unit = {
    val wrapper = TestData.createWrapper()

    TransformHeadersActor.removeHeaders(wrapper,List(new Header("foobar",null)))

    TransformHeadersActor.removeHeaders(wrapper,List(new Header("x-key",null)))
    assertFalse(wrapper.headers.exists(it => it.key=="x-key"))

    TransformHeadersActor.addHeaders(wrapper,List(new Header("x-key","abc"),new Header("x-day","abc")))
    assertEquals("abc",wrapper.getHeader("x-key"))

    TransformHeadersActor.setHeaders(wrapper,List(new Header("x-key","123")))
    assertEquals("123",wrapper.getHeader("x-key"))

  }
}
