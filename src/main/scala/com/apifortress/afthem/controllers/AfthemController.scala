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


import java.util.{Properties, UUID}

import com.apifortress.afthem._
import com.apifortress.afthem.actors.AppContext
import com.apifortress.afthem.config.{Backends, ConfigLoader, Flows}
import com.apifortress.afthem.exceptions.{BackendConfigurationMissingException, GenericException}
import com.apifortress.afthem.messages.WebRawRequestMessage
import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._
import org.springframework.web.filter.HiddenHttpMethodFilter

import scala.tools.nsc.interpreter.InputStream

/**
  * The companion object fo the main controller
  */
object AfthemController {
  val metricsLog : Logger = LoggerFactory.getLogger("_metrics.AfthemController")
  val log = LoggerFactory.getLogger(classOf[Main])
}

/**
  * The main controller
  */
@Controller
class AfthemController {

  /**
    * The action taking care of the actual proxying activity
    * @param request the inbound request
    * @return a deferred result
    */
  @RequestMapping(value = Array("**"),method = Array(RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.PATCH,RequestMethod.DELETE))
  def proxy(request: HttpServletRequest): AfthemResult = {
    val m = new Metric()
    val deferredResult = new AfthemResult
    if(request.getRequestURI == "/_afthem/status")
      return handleStatus(deferredResult)
    try {
      // find a suitable backend for the inbound URL
      val backendOption = Backends.instance.findByRequest(request)
      // if one is found then we can proceed
      if (backendOption.isDefined) {
        val backend = backendOption.get
        val flow = Flows.instance.getFlow(backend.flowId)
        val message = WebRawRequestMessage(request, backend, flow, deferredResult)
        if(backend.meta != null)
          message.meta ++= backend.meta
        message.meta.put(Metric.METRIC_START,System.nanoTime())
        AppContext.actorSystem.actorSelection("/user/proxy/request") ! message
      } else {
        // If none are found, we return an exception
        deferredResult.setData(new BackendConfigurationMissingException, ReqResUtil.STATUS_NOT_FOUND, ReqResUtil.extractAccept(request))
      }
    }catch {
      // Generic exception. Something went wrong
      case e: Exception =>
        AfthemController.log.error("Error in controller",e)
        deferredResult.setData(new GenericException("controller"),ReqResUtil.STATUS_INTERNAL, ReqResUtil.extractAccept(request))

    }
    AfthemController.metricsLog.debug(m.toString())
    return deferredResult
  }

  /**
    * Handles the call to the status endpoint
    * @param deferredResult the deferred result
    * @return the deferred result
    */
  private def handleStatus(deferredResult : AfthemResult) : AfthemResult = {
    val inputStream : InputStream = getClass().getClassLoader().getResource("META-INF/MANIFEST.MF").getContent.asInstanceOf[InputStream]
    val properties = new Properties()
    properties.load(inputStream)
    inputStream.close()

    val map = Map("clusterId" -> ConfigLoader.rootConfig.clusterId,
                  "bootstrapTime" -> Main.bootstrapTime,
                  "version" -> properties.getProperty("Implementation-Version"),
                  "springBootVersion" -> properties.get("Spring-Boot-Version"),
                  "appContextInitTime" -> AppContext.initializationTime)
    deferredResult.setData(Parsers.serializeAsJsonString(map),200,ReqResUtil.MIME_JSON)
    return deferredResult
  }

  /**
    * This configuration bean stops Spring Boot from parsing parameters before forwarding to
    * the actual action
    * @param filter
    * @return
    */
  @Bean
  def registration(filter: HiddenHttpMethodFilter) : FilterRegistrationBean[Filter] = {
    val registration = new FilterRegistrationBean[Filter](filter)
    registration.setEnabled(false)
    return registration
  }
}

