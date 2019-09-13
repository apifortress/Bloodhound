package com.apifortress.afthem

import com.apifortress.afthem.config.loaders.YamlConfigLoader
import org.junit.Test

class MainTests {

  @Test
  def testMain() : Unit = {
    YamlConfigLoader.SUBPATH = "etc.test"
    Main.main(Array.empty[String])
  }
}
