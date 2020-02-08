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

package com.apifortress.afthem.actors.proxy

import java.io.{File, FileInputStream}

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.Backend
import com.apifortress.afthem.exceptions.AfthemFlowException
import com.apifortress.afthem.messages.beans.{Header, HttpWrapper}
import com.apifortress.afthem.messages.{WebParsedRequestMessage, WebParsedResponseMessage}
import com.apifortress.afthem.{Metric, ReqResUtil, UriUtil}
import org.apache.commons.io.{FilenameUtils, IOUtils}
import org.springframework.web.util.UriComponents

/**
  * Companion object for the UpstreamFileActor
  */
object UpstreamFileActor {

  /**
    * Loads a file based on a basepath and uriComponents and safely closes the streams
    * @param basepath the basepath
    * @param uriComponents the uriComponents, coming from the request
    * @param backend the backend configuration
    * @return an Array of bytes representing the content of the file
    */
  def loadFile(basepath : String, uriComponents: UriComponents, backend: Backend) : Array[Byte] = {
    val fis = new FileInputStream(new File(basepath+File.separator+UriUtil.determineUpstreamPart(uriComponents, backend)))
    try {
      val data = IOUtils.toByteArray(fis)
      return data
    } finally {
      fis.close()
    }
  }

  def fileExtensionToContentType(extension : String) : String = {
    extension match {
      case "json" => return ReqResUtil.MIME_JSON
      case "xml" => return ReqResUtil.MIME_XML
      case _ => return ReqResUtil.MIME_PLAIN_TEXT
    }
  }
}

/**
  * In case you want to use a local file as the upstream service, this actor does it
  *
  * @param phaseId the phase ID
  */
class UpstreamFileActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {


  override def receive: Receive = {
    case msg: WebParsedRequestMessage =>
      try {
        val m = new Metric
        metricsLog.info("Processing time: "+new Metric(msg.meta.get("__process_start").get.asInstanceOf[Long]))
        metricsLog.debug("Time to Upstream: "+new Metric(msg.meta.get("__start").get.asInstanceOf[Long]))
        val data = UpstreamFileActor.loadFile(getPhase(msg).getConfigString("basepath"),
                                              msg.request.uriComponents,msg.backend)
        val headers = List(new Header("Content-Type",
                              UpstreamFileActor.fileExtensionToContentType(FilenameUtils.getExtension(msg.request.uriComponents.getPath))))
        val wrapper = new HttpWrapper("http://origin", ReqResUtil.STATUS_OK, "GET",
                                      headers, data, null, ReqResUtil.CHARSET_UTF8)
        forward(new WebParsedResponseMessage(wrapper,msg))
        metricsLog.debug(m.toString())
      }catch {
        case e : Exception =>
          log.error("Exception during the file upstream operation",e)
          throw new AfthemFlowException(msg,e.getMessage)
      }
  }
}
