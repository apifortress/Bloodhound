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

package com.apifortress.afthem.actors.filters

import com.apifortress.afthem.{Metric, ReqResUtil}
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.exceptions.{AfthemFlowException, RejectedRequestException}
import com.apifortress.afthem.messages.{ExceptionMessage, WebParsedRequestMessage}

/**
  * Actor filtering requests
  * @param phaseId the phase ID
  */
class FilterActor(phaseId : String) extends AbstractAfthemActor(phaseId : String) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>
      try {
        val m = new Metric()
        val scope = Map("msg" -> msg)

        // ACCEPT cycle. All conditions need to meet for the request to be considered accepted
        val acceptConditions = getPhase(msg).getConfigListEvalNameValue("accept")
        var accepted = acceptConditions != null

        for (item <- acceptConditions)
          if (item.evaluateIfNeeded(scope) == false)
            accepted = false

        // REJECT cycle. If at least one condition is met, then the request is rejected
        val rejectConditions = getPhase(msg).getConfigListEvalNameValue("reject")
        var rejected = false

        for (item <- rejectConditions)
          if (!rejected && item.evaluateIfNeeded(scope) == true)
            rejected = true

        // If the request has not been accepted or has been rejected, we return the error
        if(!accepted||rejected) {
          log.debug("Message rejected")
          val exceptionMessage = new ExceptionMessage(new RejectedRequestException(msg),400,msg)
          // Respond to the controller
          exceptionMessage.respond(ReqResUtil.extractAcceptFromMessage(msg, ReqResUtil.MIME_JSON))
          // and let sidecars know about the rejection
          tellSidecars(exceptionMessage)
        }
        else {
          // Otherwise, we can proceed. No sidecars here.
          log.debug("Message accepted")
          tellNextActor(msg)
        }
        metricsLog.debug(m.toString())
      }catch {
        case e : Exception =>
          log.error("Error during filtering",e)
          throw new AfthemFlowException(msg,e.getMessage)
      }
  }
}
