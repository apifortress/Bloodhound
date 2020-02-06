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
package com.apifortress.afthem

import java.util.Date

import com.apifortress.afthem.actors.AppContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.server.{WebServerFactory, WebServerFactoryCustomizer}
import org.springframework.context.annotation.{Bean, ComponentScan}
import org.apache.catalina.connector.Connector
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory

/**
  * The Main class
  */
@SpringBootApplication
@ComponentScan
class Main {

  /**
    * @return a Tomcat customizer
    */
  @Bean
  def containerCustomizer() : WebServerFactoryCustomizer[TomcatServletWebServerFactory] = {
    return new TomcatWebserverCustomizer()
  }

}

/**
  * Tomcat customizer that adds a secondary port, if needed
  */
class TomcatWebserverCustomizer extends WebServerFactoryCustomizer[TomcatServletWebServerFactory] {

  /**
    * Configuration parameter for secondary port
    */
  @Value("${server.secondary_port:-1}") var secondaryPort : Integer = null

  override def customize(factory: TomcatServletWebServerFactory): Unit = {
    if(secondaryPort > -1) {
      val connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL)
      connector.setPort(secondaryPort)
      factory.addAdditionalTomcatConnectors(connector)
    }
  }

}

/**
  * Companion object for Main
  */
object Main {

  /**
    * Logger
    */
  private val log = LoggerFactory.getLogger(classOf[Main])

  /**
    * Bootstrap time
    */
  var bootstrapTime : Date = _

  def main(args: Array[String]): Unit = {
    bootstrapTime = new Date()
    log.info("Afthem starting...")
    Runtime.getRuntime.addShutdownHook(new Thread()
    {
      override def run() : Unit =  {
        log.info("Afthem shutting down...")
        if(AppContext.actorSystem != null)
          AppContext.actorSystem.terminate()
      }
    })
    AppContext.init(SpringApplication.run(Array(classOf[Main]).asInstanceOf[Array[Class[_]]],args))
  }


}