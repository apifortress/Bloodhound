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
package com.apifortress.afthem.actors.transformers

import com.apifortress.afthem.{Metric, ReqResUtil}
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.messages.{WebParsedRequestMessage, WebParsedResponseMessage}
import com.apifortress.afthem.messages.beans.HttpWrapper

/**
  * Transform the content of a textual payload
  * @param phaseId the phaseId the phase ID
  */
class TransformPayloadActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  override def receive: Receive = {
    case msg: WebParsedRequestMessage =>
      transform(getPhase(msg),msg.request)
      forward(msg)
    case msg: WebParsedResponseMessage =>
      transform(getPhase(msg),msg.response)
      forward(msg)
  }

  /**
    * The actual transformation functionality
    * @param phase the phase
    * @param wrapper the HttpWrapper to transform
    */
  def transform(phase : Phase, wrapper: HttpWrapper) = {
    val m = new Metric()
    if(ReqResUtil.isTextPayload(wrapper)) {
      if (phase.getConfig().contains("set")) {
        val setValue = phase.getConfigString("set")
        wrapper.payload = setValue.getBytes
      } else if (phase.getConfig().contains("replace")) {
        val replace = phase.getConfigMap("replace")
        val regex = replace.get("regex").get.asInstanceOf[String]
        val value = replace.get("value").get.asInstanceOf[String]
        if(ReqResUtil.isTextPayload(wrapper)) {
          val text = ReqResUtil.byteArrayToString(wrapper)
          val t2 = text.replaceAll(regex, value)
          wrapper.payload = t2.getBytes
        }
      }
    }
    metricsLog.debug(m.toString())
  }
}
