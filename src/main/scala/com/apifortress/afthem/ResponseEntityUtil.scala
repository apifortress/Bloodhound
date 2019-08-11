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
    response.headers.foreach { header =>
      /*
       * As this gateway is meant for debugging purposes, we always decode GZIPped entities, therefore the content
       * encoding cannot be passed back as is, since the content encoding between the proxy and the client is decided
       * by the proxy configuration. If nothing is configured, Identity is the default
       */
      if(header.key != ReqResUtil.HEADER_CONTENT_ENCODING)
        envelopeBuilder = envelopeBuilder.header(header.key, header.value)
    }
    return envelopeBuilder.body(response.payload)
  }

  /**
    * Given an exception to be used as a response body and a status code, it produces a ResponseEntity off it
    * @param exception an exception
    * @param status a status code
    * @param mimeType the sanitized mime type of the response
    * @return a response entity
    */
  def createEntity(exception : Exception, status : Int, mimeType : String = ReqResUtil.MIME_JSON) : ResponseEntity[Array[Byte]] = {
    val data = mimeType match {
      case ReqResUtil.MIME_JSON =>
        exceptionToJSON(exception)
      case ReqResUtil.MIME_XML =>
        exceptionToXML(exception)
      case _ =>
        exceptionToText(exception)
    }
    return ResponseEntity.status(status).header(ReqResUtil.HEADER_CONTENT_TYPE,mimeType).body(data.getBytes(StandardCharsets.UTF_8))

  }

  /**
    * Converts an exception to a JSON message
    * @param e an exception
    * @return a JSON message
    */
  def exceptionToJSON(e : Exception): String = {
    return "{ \"status\": \"error\", \"message\": \""+StringEscapeUtils.escapeJavaScript(ExceptionUtils.getMessage(e))+"\"}\n"
  }

  /**
    * Converts an exception to an XML message
    * @param e an exception
    * @return an XML message
    */
  def exceptionToXML(e : Exception): String = {
    return "<exception><status>error</status><message>"+StringEscapeUtils.escapeXml(ExceptionUtils.getMessage(e))+"</message></exception>"
  }

  /**
    * Converts an exception to plain text message
    * @param e an exception
    * @return a plain text message
    */
  def exceptionToText(e : Exception): String = {
    return "status: error\nexception: "+ExceptionUtils.getMessage(e)
  }

}
