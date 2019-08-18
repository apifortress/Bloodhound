package com.apifortress.afthem

import org.junit.Test
import org.junit.Assert._

class ParsersTests {

  @Test
  def testParseYaml() = {
    val data = "foo:\n  bar: 22"
    assertEquals(22,
                  Parsers.parseYaml(data,classOf[Map[String,Any]])
                    .get("foo").get.asInstanceOf[Map[String,Any]].get("bar").get)
  }

  @Test
  def testParseJSON() = {
    val data = "{\"foo\":{\"bar\":22}}"
    assertEquals(22,
      Parsers.parseJSON(data,classOf[Map[String,Any]])
        .get("foo").get.asInstanceOf[Map[String,Any]].get("bar").get)
  }

  @Test
  def testParseXml() = {
    val data = "<root><foo><bar>22</bar></foo></root>"
    assertEquals("22",Parsers.parseXML(data,classOf[Map[String,Any]])
                      .get("foo").get.asInstanceOf[Map[String,Any]].get("bar").get)
  }

  @Test
  def testSerializeAsJsonString() = {
    val data = Map("foo"->Map("bar"->22))
    assertEquals("{\"foo\":{\"bar\":22}}",Parsers.serializeAsJsonString(data,false))
  }

  @Test
  def testBeautifyJSON() = {
    val data = "{\"foo\":{\"bar\":22}}"
    val beautified = "{\n  \"foo\" : {\n    \"bar\" : 22\n  }\n}"
    assertEquals(beautified, new String(Parsers.beautifyJSON(data.getBytes)))
  }

  @Test
  def testBeautifyXML() = {
    val data = "<root><foo><bar>22</bar></foo></root>"
    val beautified = "<root>\n    <foo>\n        <bar>22</bar>\n    </foo>\n</root>"
    assertEquals(beautified,new String(Parsers.beautifyXML(data.getBytes)))
  }
}
