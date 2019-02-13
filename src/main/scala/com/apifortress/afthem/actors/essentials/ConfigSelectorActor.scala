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
package com.apifortress.afthem.actors.essentials

import com.apifortress.afthem.UriUtil
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.{Backend, Backends}
import com.apifortress.afthem.messages.WebParsedRequestMessage

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
