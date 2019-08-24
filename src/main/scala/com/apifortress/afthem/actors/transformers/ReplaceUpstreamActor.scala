package com.apifortress.afthem.actors.transformers

import com.apifortress.afthem.{Metric, SpelEvaluator}
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.WebParsedRequestMessage

/**
  * Replaces the upstream URL if a certain condition happens
  * @param phaseId the phaseId the phase ID
  */
class ReplaceUpstreamActor(phaseId : String) extends AbstractAfthemActor(phaseId : String) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>
      val m = new Metric()
      val phase = getPhase(msg)
      val doReplace : Boolean = SpelEvaluator.evaluate(phase.getConfigString("expression"),
                                                          Map("msg"->msg)).asInstanceOf[Boolean]
      if(doReplace)
        msg.meta.put("__replace_upstream",phase.getConfigString("upstream"))
      metricsLog.debug(m.toString())
      forward(msg)
  }
}
