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

import com.apifortress.afthem.messages.{BaseMessage, WebParsedResponseMessage}
import com.apifortress.afthem.messages.beans.HttpWrapper

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
    obj.put("download_time",message.meta.get(Metric.METRIC_DOWNLOAD_TIME))

    obj.put("request",toExportableObject(message.request,discardRequestHeaders, true))
    obj.put("response",toExportableObject(message.response,discardResponseHeaders, false))
    return obj.toMap
  }

  def toExportableObject(wrapper : HttpWrapper, discardHeaders : List[String], isReq : Boolean) : mutable.Map[String,Any] = {
    val output = mutable.HashMap.empty[String,Any]

    // Working on request body
    if(wrapper.getPayloadSize() == 0)
      output.put("body","")
    else
      output.put("body",if(ReqResUtil.isTextPayload(wrapper)) ReqResUtil.byteArrayToString(wrapper)
      else "---BINARY---")
    output.put("size",wrapper.getPayloadSize())
    if(isReq) {
      output.put("uri", UriUtil.toSerializerUri(wrapper.uriComponents))
      output.put("request_uri", wrapper.getURL())
      output.put("querystring", wrapper.uriComponents.getQueryParams.toSingleValueMap)
      output.put("method", wrapper.method)
    }
    val headers = mutable.HashMap.empty[String,String]
    wrapper.headers.foreach { header =>
      if (discardHeaders != null && !discardHeaders.contains(header.key))
        headers.put(header.key, header.value)
    }
    output.put("headers",headers)

    if (!isReq)
      output.put("status",wrapper.status)

    return output
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
