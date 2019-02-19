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
package com.apifortress.afthem.config

import com.apifortress.afthem.{ConfigUtil, UriUtil}
import com.fasterxml.jackson.annotation.JsonProperty

/**
  * Companion object to load backends from file as a singleton
  */
object Backends  {

    def instance() : Backends = {
        return this.synchronized {
            var i2 = AfthemCache.configCache.get("backends")
            if (i2 != null) i2.asInstanceOf[Backends]
            else {
                i2 = ConfigUtil.parse[Backends]("backends.yml", classOf[Backends])
                AfthemCache.configCache.put("backends", i2)
                i2.asInstanceOf[Backends]
            }
        }
    }

}

/**
  * Data structure representing the configuration of backends
  */
class Backends(backends: List[Backend]) extends ICachableConfig {

    def findByUrl(url : String) : Option[Backend] = {
        val signature = UriUtil.getSignature(url)
        return backends.find(bec => signature.startsWith(bec.prefix))
    }
}

/**
  * The single backend configuration
  * @param prefix the inbound URI prefix
  * @param upstream the upstream URI
  */
case class Backend(@JsonProperty("flow_id") flowId: String, prefix: String, upstream: String)
