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
import com.apifortress.afthem.exceptions.AfthemFlowException
import com.apifortress.afthem.messages.{ExceptionMessage, WebParsedResponseMessage}
import com.apifortress.afthem.{Metric, ResponseEntityUtil}

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
        tellSidecars(msg.clone())

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
    metricsLog.info("Roundtrip -> "+new Metric(msg.meta.get("__start").get.asInstanceOf[Long]).toString())
  }

}
