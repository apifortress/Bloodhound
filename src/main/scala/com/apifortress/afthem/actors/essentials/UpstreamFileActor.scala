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

package com.apifortress.afthem.actors.essentials

import java.io.{File, FileInputStream}

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.beans.{Header, HttpWrapper}
import com.apifortress.afthem.messages.{WebParsedRequestMessage, WebParsedResponseMessage}
import com.apifortress.afthem.{Metric, UriUtil}
import org.apache.commons.io.IOUtils

/**
  * In case you want to use a local file as the upstream service, this actor does it
  * @param phaseId the phase ID
  */
class UpstreamFileActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {


  override def receive: Receive = {
    case msg: WebParsedRequestMessage =>
      val m = new Metric
      metricsLog.debug("Time to Request: " + new Metric(msg.meta.get("__start").get.asInstanceOf[Long]))
      val data = IOUtils.toByteArray(new FileInputStream(new File(getPhase(msg).getConfigString("subpath")+File.separator+UriUtil.determineUpstreamPart(msg.request.url,msg.backend))))
      val wrapper = new HttpWrapper("http://origin",
                              200,
                            "GET",
                                    List.empty[Header],
                                    data)
      forward(new WebParsedResponseMessage(wrapper,msg.request,msg.backend,msg.flow,msg.deferredResult,msg.date,msg.meta))
      metricsLog.debug(m.toString())
  }
}
