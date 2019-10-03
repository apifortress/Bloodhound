/**
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
package com.apifortress.afthem.config

import java.io.{File, FileReader}

import com.apifortress.afthem.Parsers
import com.apifortress.afthem.config.loaders.YamlConfigLoader
import javax.servlet.http.HttpServletRequest
import org.mockito.Mockito._
import org.junit.Assert._
import org.junit.Test

class BackendsTests {

  @Test
  def testFindByRequest() : Unit = {
    val reader = new FileReader(new File("etc.test"+File.separator+"backends.yml"))
    val backends = Parsers.parseYaml(reader,classOf[Backends])
    reader.close()
    val request = mock(classOf[HttpServletRequest])
    when(request.getRequestURL).thenReturn(new StringBuffer("http://foobar.com:8080/any/thing"))
    val found = backends.findByRequest(request).get
    assertEquals("[^/]*/any",found.prefix)
  }
  @Test
  def testFindByRequestWithHeader() : Unit = {
    val reader = new FileReader(new File("etc.test"+File.separator+"backends.yml"))
    val backends = Parsers.parseYaml(reader,classOf[Backends])
    reader.close()
    val request = mock(classOf[HttpServletRequest])
    when(request.getRequestURL).thenReturn(new StringBuffer("http://foobar.com:8080/only/with/header"))
    when(request.getHeader("x-my-header")).thenReturn("mastiff")
    val found = backends.findByRequest(request).get
    assertEquals("[^/]*/only/with/header",found.prefix)
  }

  @Test
  def testBackends() : Unit = {
    YamlConfigLoader.SUBPATH = "etc.test"
    val backends = Backends.instance()
    assertEquals(7,backends.list().size)
  }

  @Test
  def testUpstreams() : Unit = {
    YamlConfigLoader.SUBPATH = "etc.test"
    val backends = Backends.instance()
    val backend = backends.list().find(p => p.prefix=="[^/]*/upstreams").get
    assertNotNull(backend.upstreams)
    assertEquals("10 seconds",backend.upstreams.probe.timeout)
  }
}
