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

/**
  * An object wrapping information about either a request or a response
  */
class HttpWrapper(var url: String = null,
                  val status: Int = 200,
                  val method: String = null,
                  var headers: List[(String, String)] = null,
                  var payload: Array[Byte] = null,
                  val remoteIP: String = null) {

  def getHeader(name : String) : String = {
    val header = headers.find(item => item._1.toLowerCase == name.toLowerCase)
    if(header.isDefined) return header.get._2
    return ""
  }

  override def clone(): HttpWrapper = {
    new HttpWrapper(url,status,method,headers,payload,remoteIP)
  }

}
