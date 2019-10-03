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

package com.apifortress.afthem.messages.beans

import com.apifortress.afthem.messages.{BaseMessage, WebParsedResponseMessage}
import com.apifortress.afthem.{Metric, ReqResUtil, ResponseEntityUtil}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.async.DeferredResult


object AfthemResult {
  val metricsLog : Logger = LoggerFactory.getLogger("_metrics.AfthemResult")
}
/**
  * Our implementation of the deferred result.
  * @param data an HttpWrapper representing the response
  */
class AfthemResult extends DeferredResult[ResponseEntity[Array[Byte]]] {

  var m : Metric = null
  var message: BaseMessage = null

  onCompletion(() => {
       AfthemResult.metricsLog.debug("Upload time: "+m)
       if(message != null)
        AfthemResult.metricsLog.info("Roundtrip: "+new Metric(message.meta.get("__start").get.asInstanceOf[Long]).toString())
    }
  )
  /**
    * Sets the data to be sent back
    * @param data the data to be sent back
    * @param message the message, when available. We need metrics info stored in there
    */
  def setData(data : HttpWrapper, message : WebParsedResponseMessage) : Unit = {
    m = new Metric()
    this.message = message
    setResult(ResponseEntityUtil.createEntity(data))
  }

  /**
    * Sets the data to be sent back, when an exception happens
    * @param exception the exception
    * @param status the status code to be used
    * @param contentType the content type, typically the value of the accept header
    * @param message the message, when available. Useful to compute metrics information
    */
  def setData(exception : Exception, status : Int, contentType : String, message : BaseMessage = null) : Unit = {
    m = new Metric()
    this.message = message
    /*
     * Setting the result. Notice how the content type (which could be potentially anything coming from the
     * request header) gets sanitized to become a content type
     */
    setResult(ResponseEntityUtil.createEntity(exception, status, ReqResUtil.determineMimeFromContentType(contentType)))
  }

}