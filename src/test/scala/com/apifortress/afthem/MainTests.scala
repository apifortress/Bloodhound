package com.apifortress.afthem

import com.apifortress.afthem.actors.AppContext
import com.apifortress.afthem.config.loaders.YamlConfigLoader
import org.junit.Test
import org.springframework.context.ConfigurableApplicationContext

class MainTests {

  @Test
  def testMain() : Unit = {
    YamlConfigLoader.SUBPATH = "etc.test"
    Main.main(Array.empty[String])
    AppContext.springApplicationContext.asInstanceOf[ConfigurableApplicationContext].close()

  }
}
