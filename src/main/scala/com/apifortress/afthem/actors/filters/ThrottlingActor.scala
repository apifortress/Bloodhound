package com.apifortress.afthem.actors.filters

import com.apifortress.afthem.ReqResUtil
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.{AfthemCache, ApiKey}
import com.apifortress.afthem.exceptions.TooManyRequestsException
import com.apifortress.afthem.messages.{ExceptionMessage, WebParsedRequestMessage}
import com.google.common.util.concurrent.RateLimiter

class ThrottlingActor(phaseId : String) extends AbstractAfthemActor(phaseId : String) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>
      val phase = getPhase(msg)
      val keyOption = msg.meta.get("key")
      val key = if(keyOption.isDefined) keyOption.get.asInstanceOf[ApiKey].appId else null
      if(check("global", phase.getConfigInt("global",-1)) &&
         check(key, phase.getConfigInt("app_id",-1)) &&
         check(msg.request.remoteIP,phase.getConfigInt("ip_address",-1)))
        forward(msg)
      else {
        val exceptionMessage = new ExceptionMessage(new TooManyRequestsException(msg),422, msg)
        // Respond to the controller
        exceptionMessage.respond(ReqResUtil.extractAcceptFromMessage(msg, "application/json"))
        // and let sidecars know about the rejection
        tellSidecars(exceptionMessage)
      }
    }

  def check(key : String, count : Int) : Boolean = {
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
