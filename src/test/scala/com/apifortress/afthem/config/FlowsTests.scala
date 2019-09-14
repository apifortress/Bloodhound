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

import com.apifortress.afthem.config.loaders.YamlConfigLoader
import org.junit.Test
import org.junit.Assert._

class FlowsTests {

  @Test
  def testLoadFlow() : Unit = {
    YamlConfigLoader.SUBPATH = "etc.test"
    val flow = ConfigLoader.loadFlow("default")
    assertEquals(5,flow.size())
  }

  @Test
  def testFlows() : Unit = {
    YamlConfigLoader.SUBPATH = "etc.test"
    val flows = Flows.instance()
    assertEquals(5,flows.getFlow("default").size())
  }

  @Test
  def testSingleFlow() : Unit = {
    YamlConfigLoader.SUBPATH = "etc.test"
    val flows = Flows.instance()
    val flow = flows.getFlow("default")
    val requestPhase = flow.getPhase("proxy/request")
    assertEquals("proxy/request",requestPhase.id)
    val nextPhase = flow.getNextPhase(requestPhase.id)
    assertEquals("proxy/upstream_http",nextPhase.id)
  }
}
