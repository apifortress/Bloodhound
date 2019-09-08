package com.apifortress.afthem

import org.junit.Test
import org.junit.Assert._

class SpelEvaluatorTests {


  @Test
  def testEvaluate() : Unit = {
    val res = SpelEvaluator.evaluate("#foo+1",Map("foo"->1))
    assertEquals(2,res)
  }
}
