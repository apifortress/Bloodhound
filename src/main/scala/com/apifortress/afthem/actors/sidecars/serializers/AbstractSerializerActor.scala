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
package com.apifortress.afthem.actors.sidecars.serializers

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.messages.WebParsedResponseMessage


object AbstractSerializerActor {

  def shouldCapture(msg : WebParsedResponseMessage, enableOnHeader : String,
                    disableOnHeader : String, allowContentTypes : List[String]) : Boolean = {
    var go : Boolean = true
    if(enableOnHeader != null)
      go = msg.request.getHeader(enableOnHeader)!=null
    if(go && disableOnHeader != null)
      go = msg.request.getHeader(disableOnHeader)==null
    if(go && allowContentTypes.size>0){
      val ct = msg.response.getHeader("content-type")
      if(ct != null)
        go = allowContentTypes.exists( it=> ct.contains(it))
    }
    return go
  }

}
/**
  * Any actor that is going to serialize the API conversation should inherit from this class
  * @param phaseId the phaseId the phase ID
  */
abstract class AbstractSerializerActor(phaseId : String) extends AbstractAfthemActor(phaseId : String){

  /**
    * A list of request headers that will not make it to the serialized version
    */
  var discardRequestHeaders : List[String] = null

  /**
    * A list of response headers that will not make it to the serialized version
    */
  var discardResponseHeaders : List[String] = null

  /**
    * When set, requests will be serialized only if a certain header is present
    */
  var enableOnHeader : String = null

  /**
    * When present, requests will be serialized on when a certain header is not present
    */
  var disableOnHeader : String = null

  /**
    * A list of substrings that need to be present in the response content-type to enable serialization
    */
  var allowContentTypes: List[String] = null


  /**
    * Loads the configuration from the phase
    * @param phase the phase
    */
  def loadConfig(phase : Phase) = {
    discardRequestHeaders = phase.getConfigList("discard_request_headers")
    discardResponseHeaders = phase.getConfigList("discard_response_headers")
    enableOnHeader = phase.getConfigString("enable_on_header")
    disableOnHeader = phase.getConfigString("disable_on_header")
    allowContentTypes = phase.getConfigList("allow_content_types")
  }

  /**
    * Decides whether a message should be captured based on the configuration and the message
    * @param msg the message
    * @return true if the message should be captured
    */
  def shouldCapture(msg : WebParsedResponseMessage) : Boolean =
    AbstractSerializerActor.shouldCapture(msg,enableOnHeader,disableOnHeader,allowContentTypes)

}
