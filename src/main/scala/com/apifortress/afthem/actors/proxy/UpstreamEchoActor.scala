/*
 *   Copyright 2020 API Fortress
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
package com.apifortress.afthem.actors.proxy

import com.apifortress.afthem.{AfthemResponseSerializer, Parsers, ReqResUtil}
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.exceptions.AfthemFlowException
import com.apifortress.afthem.messages.beans.{Header, HttpWrapper}
import com.apifortress.afthem.messages.{WebParsedRequestMessage, WebParsedResponseMessage}

/**
  * Upstream echo that serializes the request to JSON and returns it
  * @param phaseId the phaseId the phase ID
  */
class UpstreamEchoActor(phaseId : String) extends AbstractAfthemActor(phaseId) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>
      try {
        val data = Parsers.serializeAsJsonByteArray(
                    AfthemResponseSerializer.toExportableObject(msg.request, List.empty[String], true), pretty = true)

        val wrapper = new HttpWrapper(msg.request.getURL(), ReqResUtil.STATUS_OK, "GET",
          List(new Header("Content-Type","application/json")), data, msg.request.remoteIP, ReqResUtil.CHARSET_UTF8)
        forward(new WebParsedResponseMessage(wrapper, msg.request, msg.backend, msg.flow, msg.deferredResult, msg.date, msg.meta))
      }catch {
        case e : Exception =>
          throw new AfthemFlowException(msg,e.getMessage)
      }
  }
}
