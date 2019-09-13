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
