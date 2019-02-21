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

package com.apifortress.afthem.config

import com.apifortress.afthem.ConfigUtil
import com.fasterxml.jackson.annotation.JsonProperty

/**
  * Companion class to obtain singleton Implementers instance
  */
object Implementers {
  val instance: Implementers = ConfigUtil.parse[Implementers]("implementers.yml",classOf[Implementers])

}

/**
  * A class to store the implements configuration file
  * @param implementers the list of implementers
  */
case class Implementers(implementers: List[Implementer])

/**
  * The single implementer class
  * @param id the ID of the implementer
  * @param className the class name of the implementer
  */
case class Implementer(id : String, @JsonProperty("class") className : String)
