package com.apifortress.afthem.actors.sidecars.serializers

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.messages.WebParsedResponseMessage

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

  def shouldCapture(msg : WebParsedResponseMessage) : Boolean = {
    var go : Boolean = true
    if(enableOnHeader != null)
      go = msg.request.getHeader(enableOnHeader)!=null
    if(go && disableOnHeader != null)
      go = msg.request.getHeader(disableOnHeader)==null
    if(go && allowContentTypes.size>0){
      val ct = msg.response.getHeader("content-type")
      if(ct != null)
        go = allowContentTypes.find( it=> ct.contains(it)).isDefined
    }
    return go
  }


}
