package com.apifortress.afthem.controllers

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

import com.apifortress.afthem.actors.AppContext
import com.apifortress.afthem.messages.WebRawRequestMessage
import javax.servlet.{AsyncContext, Filter, ReadListener, ServletInputStream}
import javax.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._
import org.springframework.web.context.request.async.DeferredResult
import org.springframework.web.filter.HiddenHttpMethodFilter

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

