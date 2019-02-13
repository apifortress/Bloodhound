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

import com.apifortress.afthem.ConfigUtil
import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonProperty}

/**
  * Companion object to load backends from file as a singleton
  */
object Backends {


  private var backendsInstance: Backends = null

  def load(): Backends = {
    if(backendsInstance != null)
      return backendsInstance

    backendsInstance = ConfigUtil.parse[Backends]("backends.yml",classOf[Backends])
    return backendsInstance
  }

}

/**
  * Data structure representing the configuration of backends
  */
@JsonIgnoreProperties(ignoreUnknown = true)
class Backends {

  @JsonProperty("backends")
  var backends : List[Backend] = null

}

/**
  * The single backend configuration
  * @param prefix the inbound URI prefix
  * @param upstream the upstream URI
  */
case class Backend(prefix: String, upstream: String)
