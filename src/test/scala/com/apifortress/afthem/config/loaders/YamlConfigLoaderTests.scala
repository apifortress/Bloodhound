package com.apifortress.afthem.config.loaders

import org.junit.{Before, BeforeClass, Test}
import org.junit.Assert._

object YamlConfigLoaderTests {
  @BeforeClass
  def before(): Unit ={
    YamlConfigLoader.SUBPATH = "etc.simplest"
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
