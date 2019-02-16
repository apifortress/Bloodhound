/*
 *   Copyright 2019 API Fortress
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   @author Simone Pezzano
 *
 */

package com.apifortress.afthem

import java.nio.charset.StandardCharsets

import com.apifortress.afthem.messages.beans.HttpWrapper
import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.lang.exception.ExceptionUtils
import org.springframework.http.ResponseEntity

/**
  * Util to manipulate Spring response entities
  */
object ResponseEntityUtil {

  /**
    * Given a response in the form of an HttpWrapper, it produces a ResponseEntity off it
    * @param response a response in the form of an HttpWrapper
    * @return a response entity
    */
  def createEntity(response: HttpWrapper) : ResponseEntity[Array[Byte]] = {
    var envelopeBuilder = ResponseEntity.status(response.status)
    response.headers.foreach( header=> envelopeBuilder=envelopeBuilder.header(header._1,header._2))
    return envelopeBuilder.body(response.payload)
  }

  def createEntity(data : String, status : Int) : ResponseEntity[Array[Byte]] = {
    return ResponseEntity.status(status).body(data.getBytes(StandardCharsets.UTF_8))
  }

  def createEntity(exception : Exception, status : Int) : ResponseEntity[Array[Byte]] = {
    return createEntity(exceptionToJSON(exception),status)
  }

  def exceptionToJSON(e : Exception): String = {
    return "{ \"status\": \"error\", \"message\": \""+StringEscapeUtils.escapeJavaScript(ExceptionUtils.getMessage(e))+"\"}\n"
  }

}
