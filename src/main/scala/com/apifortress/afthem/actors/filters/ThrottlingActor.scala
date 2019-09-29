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

import com.apifortress.afthem.ReqResUtil
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.{AfthemCache, ApiKey}
import com.apifortress.afthem.exceptions.TooManyRequestsException
import com.apifortress.afthem.messages.{ExceptionMessage, WebParsedRequestMessage}
import com.google.common.util.concurrent.RateLimiter

/**
  * An actor limiting the number of requests based on certain factors
  * @param phaseId the phaseId
  */
class ThrottlingActor(phaseId : String) extends AbstractAfthemActor(phaseId : String) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>

      val phase = getPhase(msg)

      // Retrieves the API key in the meta in case there's an API key in the message
      val keyOption = msg.meta.get("key")
      // If there's an API key, we extract the app ID
      val appId = if(keyOption.isDefined) keyOption.get.asInstanceOf[ApiKey].appId else null

      /*
       * Determining whether I should be throttling based on the "global" setting (all requests),
       * "app_id" setting or "ip_address" setting
       */
      if(shouldProceed("global", phase.getConfigInt("global",-1)) &&
         shouldProceed(appId, phase.getConfigInt("app_id",-1)) &&
         shouldProceed(msg.request.remoteIP,phase.getConfigInt("ip_address",-1)))
        forward(msg)
      else {
        val exceptionMessage = new ExceptionMessage(new TooManyRequestsException(msg),422, msg)
        // Respond to the controller
        exceptionMessage.respond(ReqResUtil.extractAcceptFromMessage(msg, "application/json"))
        // and let sidecars know about the rejection
        tellSidecars(exceptionMessage)
      }
    }

  /**
    * Determines whether a request should be throttled.
    * @param key the key we're using to identify the request. It could be "global", an app_id or an IP address
    * @param count the number of requests/second configured as maximum for the mode
    * @return true if the request should proceed
    */
  def shouldProceed(key : String, count : Int) : Boolean = {
    if(key == null || count <= 0)
      return true
    var rateLimiter = AfthemCache.rateLimiterCache.get(key)
    if(rateLimiter == null) {
      rateLimiter = RateLimiter.create(count)
      AfthemCache.rateLimiterCache.put(key,rateLimiter)
    }
    return rateLimiter.tryAcquire()
  }
}
