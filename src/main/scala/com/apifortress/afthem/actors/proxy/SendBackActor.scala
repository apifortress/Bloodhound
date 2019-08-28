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
package com.apifortress.afthem.actors.proxy

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.exceptions.AfthemFlowException
import com.apifortress.afthem.messages.WebParsedResponseMessage
import com.apifortress.afthem.{Metric, ReqResUtil, ResponseEntityUtil}

/**
  * The actor taking care of sending the response back to the requesting controller
  *
  * @param phaseId the phase ID
  */
class SendBackActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  override def receive: Receive = {
    case msg: WebParsedResponseMessage =>
      try {
        val m = new Metric
        tellSidecars(msg.shallowClone(true))

        /**
          * Content-Length is a sensitive header. If something in the flow changed the length of the response by
          * transformation or simply unzipping, the response body will break so we have to remove it.
          * Tomcat will replace it with the correct length once it sends the response
          */
        msg.response.removeHeader(ReqResUtil.HEADER_CONTENT_LENGTH)
        msg.response.removeHeader(ReqResUtil.HEADER_CONTENT_ENCODING)

        /**
          * If, by any chance, the upstream server returned no content-type, we set the content-type to
          * application/octet-stream as described in the rfc2616
          */
        if(!msg.response.containsHeader("content-type"))
          msg.response.setHeader("content-type","application/octet-stream")

        msg.deferredResult.setResult(ResponseEntityUtil.createEntity(msg.response))
        metricsLog.debug(m.toString())
        logProcessingTime(msg)
      }catch {
        case e : Exception => throw new AfthemFlowException(msg,e.getMessage)
      }
  }

  /**
    * Given a WebParsedResponseMessage, it retrieves the start metadata to calculate the roundtrip time and log it
    *
    * @param msg a WebParsedResponseMessage
    */
  private def logProcessingTime(msg: WebParsedResponseMessage): Unit = {
    metricsLog.info("Roundtrip: "+new Metric(msg.meta.get("__start").get.asInstanceOf[Long]).toString())
  }

}
