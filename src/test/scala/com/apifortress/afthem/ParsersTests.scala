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
