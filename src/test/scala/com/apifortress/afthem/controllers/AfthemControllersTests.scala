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
