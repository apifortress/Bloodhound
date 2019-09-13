package com.apifortress.afthem.controllers

import com.apifortress.afthem.actors.AppContext
import com.apifortress.afthem.{AfthemHttpClient, Main}
import com.apifortress.afthem.config.loaders.YamlConfigLoader
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.concurrent.FutureCallback
import org.junit.Test
import org.junit.Assert._
import org.springframework.context.ConfigurableApplicationContext

class AfthemControllersTests {

  @Test
  def testProxyController() : Unit = {
    YamlConfigLoader.SUBPATH = "etc.test"
    Main.main(Array.empty[String])
    AfthemHttpClient.execute(new HttpGet("http://127.0.0.1:8080/any"), new FutureCallback[HttpResponse] {
      override def completed(t: HttpResponse): Unit = {
        assertEquals(200,t.getStatusLine.getStatusCode)
      }

      override def failed(e: Exception): Unit = {}

      override def cancelled(): Unit = {}
    })
    Thread.sleep(5000)
    AppContext.springApplicationContext.asInstanceOf[ConfigurableApplicationContext].close()
  }
}
