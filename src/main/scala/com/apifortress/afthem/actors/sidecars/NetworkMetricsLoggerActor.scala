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
package com.apifortress.afthem.actors.sidecars

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.{BaseMessage, WebParsedResponseMessage}
import org.slf4j.LoggerFactory

/**
  * Companion object for NetworkMetricsLoggerActor
  */
object NetworkMetricsLoggerActor {

  private val TYPE_DOWNLOAD : String = "Download"

  private val networkLog = LoggerFactory.getLogger("network")

  protected def logEntry(event : String, url : String, time : Double) : Unit = {
    networkLog.info(s"[${event}] (${url}) - ${time}ms")
  }

}

/**
  * Actor to log network metrics
  * @param phaseId the phaseId
  */
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
