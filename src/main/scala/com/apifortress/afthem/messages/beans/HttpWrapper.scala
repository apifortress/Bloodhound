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
package com.apifortress.afthem.messages.beans

import com.apifortress.afthem.UriUtil
import org.springframework.web.util.UriComponents

/**
  * A wrapper suitable for both a request or a response
 *
  * @param url the URL (typically null when it's a response)
  * @param status the status code (defaults to 200)
  * @param method the method (typically null when it's a response)
  * @param headers the headers
  * @param payload the payload
  * @param remoteIP the remote IP of the requesting agent (typically null when it's a response)
  */
class HttpWrapper(private var url: String = null,
                  val status: Int = 200,
                  val method: String = null,
                  var headers: List[Header] = List.empty[Header],
                  var payload: Array[Byte] = null,
                  val remoteIP: String = null,
                  val characterEncoding: String = null) extends Serializable {

  /**
    * an UriComponents instance based on "url"
    */
  var uriComponents : UriComponents = UriUtil.toUriComponents(url)

  var callId : String = "_"


  /**
    * Sets the url and updates the uriComponents object
    * @param url the URL
    */
  def setURL(url : String) : Unit = {
    this.url = url
    uriComponents = UriUtil.toUriComponents(url)
  }

  /**
    * Returns the URL
    * @return the URL
    */
  def getURL() : String = {
    return url
  }
  /**
    * Retrieves a header value by name. Returns empty if the header was not found
    * @param name the name of the header to retrieve
    * @return the header value or empty string if not found
    */
  def getHeader(name : String) : String = {
    val header = headers.find(item => item.key.toLowerCase == name.toLowerCase)
    if(header.isDefined)
      return header.get.value
    else
      return null
  }

  def containsHeader(name : String) : Boolean = {
    return headers.count(item => item.key.toLowerCase == name.toLowerCase()) > 0
  }

  override def clone(): HttpWrapper = {
    new HttpWrapper(url,status,method,headers,payload,remoteIP,characterEncoding)
  }

  /**
    * Removes the headers given a list of header names
    * @param headerNames a list of header names
    */
  def removeHeaders(headerNames : List[String]) : Unit = {
    headers = headers.filter( header => !headerNames.contains(header.key.toLowerCase) )
  }

  /**
    * Removes a header by its name
    * @param headerName a header name
    */
  def removeHeader(headerName: String) : Unit = {
    removeHeaders(List(headerName))
  }

  /**
    * Sets a header
    * @param key the key of the header
    * @param value the value of the header
    */
  def setHeader(key: String, value : String): Unit = {
    val existingHeader = getHeader(key)
    if(existingHeader!=null)
      removeHeader(key)
    headers = headers:+new Header(key,value)
  }

  def getPayloadSize() : Long = {
    if (payload == null)
      return 0
    return payload.size
  }
}
