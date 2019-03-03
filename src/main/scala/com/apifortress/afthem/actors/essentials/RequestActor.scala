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
package com.apifortress.afthem.actors.essentials

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.beans.{Header, HttpWrapper}
import com.apifortress.afthem.messages.{WebParsedRequestMessage, WebRawRequestMessage}
import com.apifortress.afthem.{Metric, ReqResUtil, UriUtil}

/**
  * The actor in charge of parsing the inbound request data
  * @param phaseId the phase ID
  */
class RequestActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  override def receive: Receive = {
    case msg : WebRawRequestMessage =>

      val m = new Metric

      val phase = getPhase(msg)

      val parsedHeaders = ReqResUtil.parseHeaders(msg.request)

      val wrapper = new HttpWrapper(UriUtil.composeUriAndQuery(msg.request.getRequestURL.toString,msg.request.getQueryString),
                                    -1,
                                    msg.request.getMethod.toUpperCase,
                                    parsedHeaders._1,
                                    ReqResUtil.readPayload(msg.request.getInputStream(),parsedHeaders._2.get("content-length")),
                                    msg.request.getRemoteAddr)
      wrapper.removeHeaders(phase.getConfigList("discard_headers"))

      val message = new WebParsedRequestMessage(wrapper, msg.backend,
                                                msg.flow, msg.deferredResult,
                                                msg.date, msg.meta)

      forward(message)
      metricsLog.debug(m.toString())
  }

  /**
    * Given a list of headers and a list of header names, it filters the headers discarding the ones whose name appear
    * in the list
    * @param headers the headers
    * @param discardHeaders the name of the headers to discard
    * @return the filtered headers
    */
  def filterDiscardHeaders(headers: List[Header], discardHeaders: List[String]): List[Header] = {
    return headers.filter(item => !discardHeaders.contains(item.key))
  }

}
