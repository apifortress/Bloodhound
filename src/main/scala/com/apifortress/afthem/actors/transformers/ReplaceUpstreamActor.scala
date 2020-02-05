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

import com.apifortress.afthem.{Metric, SpelEvaluator, UriUtil}
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.exceptions.AfthemFlowException
import com.apifortress.afthem.messages.WebParsedRequestMessage

/**
  * Replaces the upstream URL if a certain condition happens
  * @param phaseId the phaseId the phase ID
  */
class ReplaceUpstreamActor(phaseId : String) extends AbstractAfthemActor(phaseId : String) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>
      try {
        val m = new Metric()
        val phase = getPhase(msg)
        val doReplace: Boolean = SpelEvaluator.evaluate(phase.getConfigString("expression"),
          Map("msg" -> msg)).asInstanceOf[Boolean]
        if (doReplace) {
          val upstream = SpelEvaluator.evaluateStringIfNeeded(phase.getConfigString("upstream"),Map("msg" -> msg,"UriUtil" -> UriUtil))
          msg.meta.put("__replace_upstream", upstream)
        }
        metricsLog.debug(m.toString())
        forward(msg)
      }catch {
        case e : Exception =>
          log.error("Error while replacing upstream",e)
          throw new AfthemFlowException(msg,e.getMessage)
      }
  }
}
