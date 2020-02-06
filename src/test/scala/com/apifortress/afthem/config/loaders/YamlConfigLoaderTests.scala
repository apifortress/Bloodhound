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
package com.apifortress.afthem.config.loaders

import org.junit.{Before, BeforeClass, Test}
import org.junit.Assert._

object YamlConfigLoaderTests {
  @BeforeClass
  def before(): Unit ={
    YamlConfigLoader.SUBPATH = "etc.test"
  }
}
class YamlConfigLoaderTests {


  @Test
  def testLoadAfthemRootConf() = {
    val configLoader = new YamlConfigLoader()
    val conf = configLoader.loadAfthemRootConf()
    assertTrue(conf.mime.textContentTypeContain.size>0)
    assertEquals("com.apifortress.afthem.config.loaders.YamlConfigLoader",conf.configLoader.className)
  }

  @Test
  def testLoadBackends() = {
    val configLoader = new YamlConfigLoader()
    val backends = configLoader.loadBackends()
    assertTrue(backends.list().size>0)
  }

  @Test
  def testLoadFlow() = {
    val configLoader = new YamlConfigLoader()
    val backends = configLoader.loadBackends()
    backends.list().foreach{ it =>
      val flow = configLoader.loadFlow(it.flowId)
      assertTrue(flow.size()>0)
    }
  }

  @Test
  def testLoadImplementers() = {
    val implementers = new YamlConfigLoader().loadImplementers()
    assertTrue(implementers.implementers.size>0)
  }
}
