package com.apifortress.afthem.actors.sidecars

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.{BaseMessage, WebParsedResponseMessage}
import org.slf4j.LoggerFactory

object NetworkMetricsLoggerActor {

  private val TYPE_DOWNLOAD : String = "Download"

  private val networkLog = LoggerFactory.getLogger("network")

  protected def logEntry(event : String, url : String, time : Double) : Unit = {
    networkLog.info(s"[${event}] (${url}) - ${time}ms")
  }

}
class NetworkMetricsLoggerActor(phaseId : String) extends AbstractAfthemActor(phaseId : String) {

  override def receive: Receive = {
    case msg : WebParsedResponseMessage =>
      val downloadTime = msg.meta.get("__download_time")
      if(downloadTime.isDefined)
        NetworkMetricsLoggerActor.logEntry(NetworkMetricsLoggerActor.TYPE_DOWNLOAD,
                                            msg.response.getURL(),
                                            downloadTime.get.asInstanceOf[Double])
  }
}
