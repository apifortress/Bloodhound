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


import com.apifortress.afthem.ReqResUtil
import com.apifortress.afthem.actors.AppContext
import com.apifortress.afthem.config.{Backends, Flows}
import com.apifortress.afthem.exceptions.{BackendConfigurationMissingException, GenericException}
import com.apifortress.afthem.messages.WebRawRequestMessage
import com.apifortress.afthem.messages.beans.AfthemResult
import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._
import org.springframework.web.filter.HiddenHttpMethodFilter

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
    val deferredResult = new AfthemResult
    try {
      // find a suitable backend for the inbound URL
      val backendOption = Backends.instance.findByRequest(request)
      // if one is found then we can proceed
      if (backendOption.isDefined) {
        val backend = backendOption.get
        val flow = Flows.instance.getFlow(backend.flowId)
        val message = WebRawRequestMessage(request, backend, flow, deferredResult)
        message.meta.put("__start",System.nanoTime())
        AppContext.actorSystem.actorSelection("/user/proxy/request") ! message
      } else {
        // If none are found, we return an exception
        deferredResult.setData(new BackendConfigurationMissingException, 404, ReqResUtil.extractAccept(request))
      }
    }catch {
      // Generic exception. Something went wrong
      case e: Exception =>
        deferredResult.setData(new GenericException("controller"),500, ReqResUtil.extractAccept(request))

    }

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

