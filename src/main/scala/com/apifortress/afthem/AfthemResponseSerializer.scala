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

import com.apifortress.afthem.messages.WebParsedResponseMessage

import scala.collection.mutable

/**
  * Utility to convert a WebParsedResponseMessage into a Map that is compatible with the API Fortress platform
  * expectations. The map can then be converted into a JSON for proper serialization
  */
object AfthemResponseSerializer {

  /**
    * Before any valid serialization takes place, the data needs to be transformed in a data structure
    * we agree upon
    *
    * @param message a WebParsedResponseMessage object
    * @param discardRequestHeaders a list of request header names you want to omit
    * @param discardResponseHeaders a list of response header names you want to omit
    * @return the message, converted to the agreed data structure
    */
  def toExportableObject(message: WebParsedResponseMessage,
                         discardRequestHeaders : List[String] = List.empty[String],
                         discardResponseHeaders : List[String] = List.empty[String]): Map[String,Any] = {
    val obj = mutable.HashMap.empty[String,Any]
    obj.put("client_ip",message.request.remoteIP)
    obj.put("started_at",message.date.getTime)
    obj.put("download_time",message.meta.get("__download_time"))
    val request = mutable.HashMap.empty[String,Any]
    request.put("body",if(ReqResUtil.isTextPayload(message.request))
                          ReqResUtil.byteArrayToString(message.request)
                            else "---BINARY---")
    request.put("size",message.request.payload.length)
    request.put("uri",UriUtil.toSerializerUri(message.request.uriComponents))
    request.put("request_uri",message.request.getURL())
    request.put("querystring",message.request.uriComponents.getQueryParams.toSingleValueMap)
    request.put("method",message.request.method)
    val requestHeaders = mutable.HashMap.empty[String,String]
    message.request.headers.foreach { header =>
      if (discardRequestHeaders != null && !discardRequestHeaders.contains(header.key))
        requestHeaders.put(header.key, header.value)
    }
    request.put("headers",requestHeaders)
    obj.put("request",request)

    val response = mutable.HashMap.empty[String,Any]
    response.put("body",if(ReqResUtil.isTextPayload(message.response))
                            ReqResUtil.byteArrayToString(message.response)
                              else "---BINARY---")
    response.put("size",message.response.payload.length)
    response.put("status",message.response.status)
    val responseHeaders = mutable.HashMap.empty[String,String]
    message.response.headers.foreach{ header =>
      if (discardResponseHeaders != null && !discardResponseHeaders.contains(header.key))
        responseHeaders.put(header.key,header.value)
    }
    response.put("headers",responseHeaders)
    obj.put("response",response)

    return obj.toMap
  }
  /**
    * Serializes a WebParsedResponseMessage to string
    *
    * @param message a WebParsedResponseMessage object
    * @param discardRequestHeaders a list of request header names you want to omit
    * @param discardResponseHeaders a list of response header names you want to omit
    * @return the serialized version of the object
    */
  def serialize(message: WebParsedResponseMessage,
                discardRequestHeaders : List[String] = List.empty[String],
                discardResponseHeaders : List[String] = List.empty[String]): String = {
    val obj = toExportableObject(message, discardRequestHeaders, discardResponseHeaders)
    return Parsers.serializeAsJsonString(obj, pretty = false)
  }
}
