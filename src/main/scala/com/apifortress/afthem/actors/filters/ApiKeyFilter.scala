package com.apifortress.afthem.actors.filters

import java.io.{File, FileInputStream}

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.{AfthemCache, ApiKeys}
import com.apifortress.afthem.exceptions.UnauthorizedException
import com.apifortress.afthem.messages.{ExceptionMessage, WebParsedRequestMessage}
import com.apifortress.afthem.{Parsers, ReqResUtil}
import org.apache.commons.io.IOUtils

/**
  * Basic API Key filter loading keys from a YAML file
  * @param phaseId the phaseId the phase ID
  */
class ApiKeyFilter(phaseId : String) extends AbstractAfthemActor(phaseId : String) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>
      val phase = getPhase(msg)
      val foundKey = determineKey(phase.getConfigString("in"),phase.getConfigString("name"),msg)
      val key = loadKeys(phase.getConfigString("filename","apikeys.yml")).getApiKey(foundKey)
      if(key.isDefined){
        msg.meta.put("key",key.get)
        tellNextActor(msg)
      } else {
        log.debug("Message rejected")
        val exceptionMessage = new ExceptionMessage(new UnauthorizedException(msg),401,msg)
        // Respond to the controller
        exceptionMessage.respond(ReqResUtil.extractAcceptFromMessage(msg,"application/json"))
        // and let sidecars know about the rejection
        tellSidecars(exceptionMessage)
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
    val data = IOUtils.toString(fis,"UTF-8")
    fis.close()
    val keys = Parsers.parseYaml(data,classOf[ApiKeys])
    AfthemCache.apiKeysCache.put(filename,keys)
    return keys
  }

  /**
    * Determines the API key provided in the request
    * @param in "query" or "header". Where the API key is expected to appear
    * @param name the name of the query parameter of header meant to carry the key
    * @param msg the inbound message
    * @return the found key, if any
    */
  private def determineKey(in : String, name : String, msg : WebParsedRequestMessage) : String = {
    val found = in match {
      case "header" => msg.request.getHeader(name)
      case "query" =>
        val nvp = ReqResUtil.parseQueryString(msg.request.url).find(item => item.getName==name)
        if(nvp.isDefined)
          nvp.get.getValue
        else null
    }
    return found
  }
}