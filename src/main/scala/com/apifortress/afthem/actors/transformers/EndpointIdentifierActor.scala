package com.apifortress.afthem.actors.transformers

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.exceptions.AfthemFlowException
import com.apifortress.afthem.messages.beans.HttpWrapper
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage, WebParsedResponseMessage}

import util.control.Breaks._

class EndpointIdentifierActor(phaseId : String) extends AbstractAfthemActor(phaseId : String) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>
      perform(msg.request,msg)
    case msg : WebParsedResponseMessage =>
      perform(msg.request,msg)
  }

  def perform(request : HttpWrapper, msg : BaseMessage) ={
    try {
      val kv = getPhase(msg).getConfigMap("regex")
      var foundName : String = null
      breakable {
        kv.foreach { item =>
          val name = item._1
          val patterns = item._2.asInstanceOf[Map[String,String]]

          // Matching URL
          val urlPattern = patterns("url")
          val urlResult = request.getURL().matches(urlPattern)

          val methodResult = if(patterns.contains("method")) request.method.matches(patterns("method")) else true

          if (urlResult && methodResult) {
            foundName = name
            break
          }
        }
      }
      if(foundName != null)
        request.callId = foundName
      forward(msg)
    }catch {
      case e : Exception =>
        log.error("Exception during the add meta operation",e)
        throw new AfthemFlowException(msg,e.getMessage)
    }
  }
}
