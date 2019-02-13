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
import com.apifortress.afthem.messages.{HttpWrapper, WebParsedRequestMessage, WebRawRequestMessage}
import com.apifortress.afthem.{ReqResUtil, UriUtil}
import org.slf4j.LoggerFactory

object RequestParserActor {

  val log = LoggerFactory.getLogger("RequestParserActor")

}
class RequestParserActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  override def receive: Receive = {
    case msg : WebRawRequestMessage => {
      val wrapper = new HttpWrapper

      wrapper.url = UriUtil.composeUriAndQuery(msg.request.getRequestURL.toString,msg.request.getQueryString)

      val parsedHeaders = ReqResUtil.parseHeaders(msg.request,phase.config.get("discard_headers")
                                    .getOrElse(List.empty[String]).asInstanceOf[List[String]])

      wrapper.headers = parsedHeaders._1

      wrapper.payload = ReqResUtil.readPayload(msg.request.getInputStream,parsedHeaders._2.get("content-length"))

      wrapper.method = msg.request.getMethod.toUpperCase

      wrapper.remoteIP = msg.request.getRemoteAddr

      val message = new WebParsedRequestMessage(wrapper,msg.deferredResult,msg.date,msg.meta)

      forward(message)
    }
  }

}
