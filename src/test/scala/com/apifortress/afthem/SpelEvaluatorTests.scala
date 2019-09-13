package com.apifortress.afthem

import com.apifortress.afthem.config.loaders.YamlConfigLoader
import org.junit.Test
import org.junit.Assert._

class SpelEvaluatorTests {


  @Test
  def testEvaluate() : Unit = {
    YamlConfigLoader.SUBPATH = "etc.test"
    val res = SpelEvaluator.evaluate("#foo+1",Map("foo"->1))
    assertEquals(2,res)
  }
}
