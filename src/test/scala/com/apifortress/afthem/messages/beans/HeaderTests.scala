package com.apifortress.afthem.messages.beans

import org.junit.Test
import org.junit.Assert._
class HeaderTests {

  @Test
  def testHeader() = {
    val header = new Header("Foo","bar")
    assertEquals("foo",header.key)
  }
}
