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
package com.apifortress.afthem.actors.transformers

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.exceptions.AfthemFlowException
import com.apifortress.afthem.messages.{WebParsedRequestMessage, WebParsedResponseMessage}
import com.apifortress.afthem.{Metric, Parsers, ReqResUtil}
import org.slf4j.Logger


/**
  * Companion object for BeautifyPayloadACtor
  */
object BeautifyPayloadActor {
  /**
    * The actual beautification functionality
    * @param data the data
    * @param mode the mode (must contain either 'json' or 'xml')
    * @return the beautificated content
    */
  def beautify(data : Array[Byte], mode : String, log : Logger) : Array[Byte] = {
    try {
      if (mode.contains("json"))
        return Parsers.beautifyJSON(data)
      if (mode.contains("xml"))
        return Parsers.beautifyXML(data)
    }catch {
      case e : Exception => log.warn("Invalid format")
    }
    return data
  }
}
/**
  * Actor that beautifies a request or response payload
  * @param phaseId the phase ID
  */
class BeautifyPayloadActor(phaseId : String) extends AbstractAfthemActor(phaseId : String) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>
      try {
        val m = new Metric
        val phase = getPhase(msg)
        msg.request.payload = BeautifyPayloadActor.beautify(msg.request.payload,
                                                            phase.config.getOrElse("mode","json").asInstanceOf[String],
                                                            log)
        msg.request.removeHeader(ReqResUtil.HEADER_CONTENT_LENGTH)
        forward(msg)
        metricsLog.debug(m.toString())
      }catch {
        case e : Exception =>
          log.error("Exception during the beautify payload operation",e)
          throw new AfthemFlowException(msg,e.getMessage)
      }

    case msg : WebParsedResponseMessage =>
      try{
        val m = new Metric
        val phase = getPhase(msg)
        msg.response.payload = BeautifyPayloadActor.beautify(msg.response.payload,
                                                             phase.config.getOrElse("mode","json").asInstanceOf[String],
                                                             log)
        msg.response.removeHeader(ReqResUtil.HEADER_CONTENT_LENGTH)
        forward(msg)
        metricsLog.debug(m.toString())
      }catch {
        case e : Exception =>
          log.error("Exception during the beautify payload operation",e)
          throw new AfthemFlowException(msg,e.getMessage)
      }

  }


}
