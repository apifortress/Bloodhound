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
package com.apifortress.afthem.actors.filters

import java.io.{File, FileInputStream}

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.{AfthemCache, ApiKey, ApiKeys, Phase}
import com.apifortress.afthem.exceptions.{AfthemFlowException, UnauthorizedException}
import com.apifortress.afthem.messages.{ExceptionMessage, WebParsedRequestMessage}
import com.apifortress.afthem.{Metric, Parsers, ReqResUtil}
import org.apache.commons.io.IOUtils

/**
  * Basic API Key filter loading keys from a YAML file
  * @param phaseId the phaseId the phase ID
  */
class ApiKeyFilterActor(phaseId : String) extends AbstractAfthemActor(phaseId : String) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>
      try {
        val m = new Metric()
        val phase = getPhase(msg)
        val foundKey = determineKey(phase.getConfigString("in"), phase.getConfigString("name"), msg)
        val key = findKey(foundKey, phase)
        if(key.isDefined && key.get.enabled){
          msg.meta.put("key",key.get)
          tellNextActor(msg)
        } else {
          log.debug("Message rejected")
          val exceptionMessage = new ExceptionMessage(new UnauthorizedException(msg), 401, msg)
          // Respond to the controller
          exceptionMessage.respond(ReqResUtil.extractAcceptFromMessage(msg, "application/json"))
          // and let sidecars know about the rejection
          tellSidecars(exceptionMessage)
        }
        metricsLog.debug(m.toString())
      }catch {
        case e : Exception =>
          log.error("Error during API-key filtering",e)
          throw new AfthemFlowException(msg,e.getMessage)
      }

  }

  /**
    * Load API keys from file
    * @param filename the name of the file load the keys from
    * @return the ApiKeys
    */
  private def loadKeys(filename : String) : ApiKeys = {
    val cachedItem = AfthemCache.apiKeysCache.get(filename)
    if(cachedItem != null)
      return cachedItem
    val fis = new FileInputStream(new File(filename))
    val data = IOUtils.toString(fis,ReqResUtil.CHARSET_UTF8)
    fis.close()
    val keys = Parsers.parseYaml(data,classOf[ApiKeys])
    AfthemCache.apiKeysCache.put(filename,keys)
    return keys
  }

  /**
    * Finds a key, if any. Implementation dependent
    * @param key the key to look for
    * @param phase the phase
    * @return an optional ApiKey object
    */
  protected def findKey(key : String, phase : Phase) : Option[ApiKey] = {
    return loadKeys(phase.getConfigString("filename","apikeys.yml")).getApiKey(key)
  }

  /**
    * Determines the API key provided in the request
    * @param in "query" or "header". Where the API key is expected to appear
    * @param name the name of the query parameter of header meant to carry the key
    * @param msg the inbound message
    * @return the found key, if any
    */
  protected def determineKey(in : String, name : String, msg : WebParsedRequestMessage) : String = {
    val found = in match {
      case "header" => msg.request.getHeader(name)
      case "query" => msg.request.uriComponents.getQueryParams.getFirst(name)
    }
    return found
  }
}