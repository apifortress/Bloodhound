package com.apifortress.afthem.actors.transformers

import org.junit.Test
import org.junit.Assert._

class BeautifyPayloadActorTests {

  @Test
  def testBeautificationJson() : Unit = {
    val data = "{\"foo\":\"bar\"}"
    val result = BeautifyPayloadActor.beautify(data.getBytes,"json",null)
    assertEquals("{\n  \"foo\" : \"bar\"\n}",new String(result))
  }

  @Test
  def testBeautificationXml() : Unit = {
    val data = "<root attr=\"attr1\"><foo>bar</foo></root>"
    val result = BeautifyPayloadActor.beautify(data.getBytes,"xml",null)
    assertEquals("<root attr=\"attr1\">\n    <foo>bar</foo>\n</root>", new String(result))
  }
}
