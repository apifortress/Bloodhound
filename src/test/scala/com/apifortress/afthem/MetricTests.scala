package com.apifortress.afthem

import org.junit.Test
import org.junit.Assert._

class MetricTests {

  @Test
  def testMetric() : Unit = {
    val metric = new Metric()
    val start = System.currentTimeMillis()
    Thread.sleep(50)
    assertTrue(start+metric.time().toLong >= start+50)
    assertFalse(start+metric.time().toLong >= start+500)
    assertTrue(metric.toString().contains("ms"))
  }
}
