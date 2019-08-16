package com.apifortress.afthem.actors.transformers

import com.apifortress.afthem.ReqResUtil
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.messages.{WebParsedRequestMessage, WebParsedResponseMessage}
import com.apifortress.afthem.messages.beans.HttpWrapper

class TransformPayloadActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  override def receive: Receive = {
    case msg: WebParsedRequestMessage =>
      transform(getPhase(msg),msg.request)
      forward(msg)
    case msg: WebParsedResponseMessage =>
      transform(getPhase(msg),msg.response)
      forward(msg)
  }


  def transform(phase : Phase, wrapper: HttpWrapper) = {
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
  }
}
