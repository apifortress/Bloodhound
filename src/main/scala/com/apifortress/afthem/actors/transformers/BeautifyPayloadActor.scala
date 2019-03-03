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
import com.apifortress.afthem.{Metric, Parsers}

/**
  * Actor that beautifies a request or response payload
  * @param phaseId the phase ID
  */
class BeautifyPayloadActor(phaseId : String) extends AbstractAfthemActor(phaseId : String) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>
      try {
        val m = new Metric
        msg.request.payload = beautify(msg.request.payload,getPhase(msg).config.getOrElse("mode","json").asInstanceOf[String])
        msg.request.removeHeader("content-length")
        forward(msg)
        metricsLog.debug(m.toString())
      }catch {
        case e : Exception => throw new AfthemFlowException(msg,e.getMessage)
      }

    case msg : WebParsedResponseMessage =>
      try{
        val m = new Metric
        msg.response.payload = beautify(msg.response.payload,getPhase(msg).config.getOrElse("mode","json").asInstanceOf[String])
        msg.response.removeHeader("content-length")
        forward(msg)
        metricsLog.debug(m.toString())
      }catch {
        case e : Exception => throw new AfthemFlowException(msg,e.getMessage)
      }

  }

  /**
    * The actual beautification functionality
    * @param data the data
    * @param mode the mode (must contain either 'json' or 'xml')
    * @return the beautificated content
    */
  def beautify(data : Array[Byte], mode : String) : Array[Byte] = {
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
