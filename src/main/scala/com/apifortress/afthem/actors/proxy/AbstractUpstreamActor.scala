package com.apifortress.afthem.actors.proxy

import com.apifortress.afthem.Metric
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.BaseMessage

abstract class AbstractUpstreamActor(phaseId : String) extends AbstractAfthemActor(phaseId) {

  def logUpstreamMetrics(msg : BaseMessage) : Unit = {
    metricsLog.info("Processing time: "+new Metric(msg.meta.get(Metric.METRIC_PROCESS_START).get.asInstanceOf[Long]))
    metricsLog.debug("Time to Upstream: "+new Metric(msg.meta.get(Metric.METRIC_START).get.asInstanceOf[Long]))
  }

}
