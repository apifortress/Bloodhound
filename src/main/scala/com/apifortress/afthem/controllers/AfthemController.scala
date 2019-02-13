package com.apifortress.afthem.controllers


import com.apifortress.afthem.actors.AppContext
import com.apifortress.afthem.messages.WebRawRequestMessage
import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._
import org.springframework.web.context.request.async.DeferredResult
import org.springframework.web.filter.HiddenHttpMethodFilter

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
@Controller
class AfthemController {


  @RequestMapping(value = Array("**"),method = Array(RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.PATCH,RequestMethod.DELETE))
  def proxy(request: HttpServletRequest): DeferredResult[ResponseEntity[Array[Byte]]] = {
    val deferredResult = new DeferredResult[ResponseEntity[Array[Byte]]]

    AppContext.getActorByPhaseId("request_parser") ! new WebRawRequestMessage(request,deferredResult)

    return deferredResult
  }

  @Bean
  def registration(filter: HiddenHttpMethodFilter) : FilterRegistrationBean[Filter] = {
    val registration = new FilterRegistrationBean[Filter](filter)
    registration.setEnabled(false)
    return registration
  }
}

