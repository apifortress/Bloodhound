package com.apifortress.afthem.actors.essentials

import com.apifortress.afthem.UriUtil
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.{Backend, Backends}
import com.apifortress.afthem.messages.WebParsedRequestMessage

/**
  * The Actor in charge to load the proper configuration for the current request
  */
class ConfigSelectorActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>
      val backendConfigs =  Backends.load()
      val signature : String = UriUtil.getSignature(msg.request.url)
      val cfg : Option[Backend] = backendConfigs.backends.find(bec => signature.startsWith(bec.prefix))
      if(cfg.nonEmpty) {
        log.debug("One configuration found for this request")
        msg.backendConfig = cfg.get
        forward(msg)
    }
  }

}
