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

import java.io.InputStream
import java.net.URL
import java.nio.charset.Charset

import com.apifortress.afthem.config.ConfigLoader
import com.apifortress.afthem.messages.beans.{Header, HttpWrapper}
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage, WebParsedResponseMessage, WebRawRequestMessage}
import javax.servlet.http.HttpServletRequest
import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.BoundedInputStream
import org.apache.http.HttpResponse
import org.apache.http.entity.ContentType

import scala.collection.mutable

/**
  * Utils to handle requests and responses
  */
object ReqResUtil {

  /**
    * The name of the content-length header
    */
  val HEADER_CONTENT_LENGTH : String = "content-length"

  /**
    * The name of the content-type header
    */
  val HEADER_CONTENT_TYPE : String = "content-type"

  /**
    * The name of the content-encoding header
    */
  val HEADER_CONTENT_ENCODING : String = "content-encoding"


  val HEADER_TRANSFER_ENCODING : String = "transfer-encoding"

  val HEADER_CONNECTION : String = "connection"

  /**
    * The name of the host header
    */
  val HEADER_HOST : String = "host"

  /**
    * The name of the accept header
    */
  val HEADER_ACCEPT : String = "accept"

  /**
    * application/json mime
    */
  val MIME_JSON = "application/json"

  /**
    * text/xml mime
    */
  val MIME_XML = "text/xml"

  /**
    * text/plain mime
    */
  val MIME_PLAIN_TEXT = "text/plain"

  /**
    * UTF-8 charset
    */
  val CHARSET_UTF8 = "UTF-8"

  /**
    * Parses servlet headers into a list of tuples and collects a map of interesting headers
    * @param request an HttpServletRequest to parse the headers from
    * @return a tuple made up of (list of headers, interesting headers)
    */
  def parseHeaders(request: HttpServletRequest, discardHeaders : mutable.MutableList[String] = mutable.MutableList.empty[String]) : (List[Header],Map[String,Any]) = {
    val headers = new mutable.MutableList[Header]
    val interestingHeaders = new mutable.HashMap[String,Any]

    val headerNames = request.getHeaderNames
    while(headerNames.hasMoreElements){
      val headerName = headerNames.nextElement()
      headerName match {
        case HEADER_CONTENT_LENGTH =>
          interestingHeaders.put(HEADER_CONTENT_LENGTH,request.getHeader(HEADER_CONTENT_LENGTH).toInt)
        case HEADER_CONTENT_TYPE =>
          interestingHeaders.put(HEADER_CONTENT_TYPE,request.getHeader(HEADER_CONTENT_TYPE))
        case _ =>
      }
      if (!discardHeaders.contains(headerName.toLowerCase))
        headers+=new Header(headerName,request.getHeader(headerName))
    }
    return (headers.toList,interestingHeaders.toMap)
  }

  /**
    * Parses response headers into a list of tuples and collects a map of interesting headers
    * @param response an HttpResponse object
    * @return a tuple made up of (list of headers, interesting headers)
    */
  def parseHeaders(response: HttpResponse) : (List[Header],Map[String,Any]) = {
    val headers = new mutable.MutableList[Header]
    val interestingHeaders = new mutable.HashMap[String,Any]
    response.getAllHeaders.foreach{ header =>
      headers+=new Header(header.getName,header.getValue)
      header.getName match {
        case HEADER_CONTENT_LENGTH =>
          interestingHeaders.put(HEADER_CONTENT_LENGTH, header.getValue.toInt)
        case HEADER_CONTENT_TYPE =>
          interestingHeaders.put(HEADER_CONTENT_TYPE, header.getValue)
        case _ =>
      }
    }
    return (headers.toList,interestingHeaders.toMap)
  }

  /**
    * Reads a payload from an input stream. It will conform to what's told in the content-length header,
    * if present, or continue to EOF
    * @param inputStream the input stream to read from
    * @param contentLength an optional content-length header value
    * @return a byte array
    */
  def readPayload(inputStream: InputStream, contentLength: Option[Any]) : Array[Byte] = {
    if(inputStream == null)
      return Array.empty[Byte]
    val boundedInputStream = new BoundedInputStream(inputStream)
    var data: Array[Byte] = null
    try {
      if (contentLength.isDefined)
      // If content-length is defined, then we conform to it
        data = IOUtils.readFully(boundedInputStream, contentLength.get.asInstanceOf[Int])
      else
      // Otherwise we read to EOF
        data = IOUtils.toByteArray(boundedInputStream)
    } finally {
      // We want to make sure we're closing the streams
      boundedInputStream.close()
      inputStream.close()
    }
    return data
  }

  /**
    * Extracts the "accept" header from an HttpServletRequest
    * @param request the request
    * @param default the default value, in case no "accept" header is present
    * @return the value of the accept header
    */
  def extractAccept(request : HttpServletRequest, default : String = MIME_JSON) : String = {
    val accept = request.getHeader(HEADER_ACCEPT)
    if(accept == null)
      return default
    return accept
  }

  /**
    * Extracts the "accept" header from either a WebParsedRequestMessage or a WebParsedResponseMessage
    * @param message the message
    * @param default the default value in case no "accept" header is present
    * @return the value of the accept header
    */
  def extractAcceptFromMessage(message : BaseMessage, default : String = MIME_JSON) : String = {
    val request = message match {
      case message : WebParsedRequestMessage => message.asInstanceOf[WebParsedRequestMessage].request
      case message : WebParsedResponseMessage => message.asInstanceOf[WebParsedResponseMessage].request
      case message : WebRawRequestMessage => null
    }
    if(request == null)
      return default
    val headerValue = request.getHeader(HEADER_ACCEPT)
    if(headerValue == null)
      return default
    return headerValue
  }

  /**
    * Given an wrapper (request or response), it searches for a the content-type header. If absent, default is returned
    * @param wrapper the HttpWrapper
    * @param default the default value in case of miss
    * @return the content-type
    */
  def extractContentType(wrapper : HttpWrapper, default : String = null): String = {
    val headerValue = wrapper.getHeader("content-type")
    if(headerValue == null)
      return default
    return headerValue
  }

  /**
    * Selects a mime type between JSON, XML or TEXT, based off a content-type. This method should be used when
    * AFtheM needs to return an internally generated message for the user
    * @param contentType a content-type
    * @return a sanitized mime type
    */
  def determineMimeFromContentType(contentType : String) : String = {
    if(contentType.contains("json"))
      return MIME_JSON
    if(contentType.contains("xml"))
      return MIME_XML
    return MIME_PLAIN_TEXT
  }


  /**
    * Given a content-type string, it will search whether there's a match in the afthem.yml configuration. If found
    * it means that the content type represents text. If the provided content-type is null, the return value is true
    * @param contentType the content-type to be evaluated
    * @return true, if afthem.yml says that the content type represents text
    */
  def isText(contentType : String) : Boolean = {
    if(contentType==null)
      return false
    return ConfigLoader.rootConfig.mime.textContentTypeContain.find( pattern => contentType.contains(pattern)).isDefined
  }

  /**
    * Given an HttpWrapper, it will try to determine whether the carried payload is text or binary
    * @param wrapper an HttpWrapper
    * @return true if the payload is binary
    */
  def isTextPayload(wrapper: HttpWrapper) : Boolean = {
    return isText(extractContentType(wrapper,null))
  }

  /**
    * Extracts charset information from the response
    * @param httpResponse an HttpResponse
    * @param default a default value in case charset information is not present
    * @return the detected charset
    */
  def getCharsetFromResponse(httpResponse: HttpResponse, default : String = ReqResUtil.CHARSET_UTF8) : String = {
    val contentType = ContentType.get(httpResponse.getEntity)
    if(contentType == null)
      return default
    val charset = contentType.getCharset
    if(charset == null)
      return default
    return charset.name()
  }

  /**
    * Extracts charset information from the request
    * @param httpServletRequest an HttpServletRequest
    * @param default the default value in case charset information is not present
    * @return the detected charset
    */
  def getCharsetFromRequest(httpServletRequest: HttpServletRequest, default : String = ReqResUtil.CHARSET_UTF8) : String = {
    val headerValue = httpServletRequest.getCharacterEncoding
    if(headerValue == null)
      return default
    return headerValue
  }

  /**
    * Convert the payload stored as bytes in the wrapper, into a String
    * @param wrapper the HttpWrapper
    * @return the string
    */
  def byteArrayToString(wrapper : HttpWrapper) : String = {
    val characterEncoding = if (wrapper.characterEncoding!=null)
                            wrapper.characterEncoding
                          else
                            ReqResUtil.CHARSET_UTF8
    new String(wrapper.payload,Charset.forName(characterEncoding))
  }

}
