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
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
  * Companion object to load phases from file as a singleton
  */
object Phases {

  private val objectMapper: ObjectMapper = new ObjectMapper(new YAMLFactory())
  objectMapper.registerModule(DefaultScalaModule)

  private var phasesInstance : Phases = null

  def load(): Phases = {
    if(phasesInstance != null)
      return phasesInstance
    phasesInstance = ConfigUtil.parse[Phases]("phases.yml", classOf[Phases])
    return phasesInstance
  }
}
class Phases {

  var phases : Map[String,Phase] = null

  def getPhase(id: String) : Phase = {
    val phase = phases.get(id).get
    phase.id = id
    return phase
  }
  def getNextPhase(id: String) : Phase = {
    return getPhase(getPhase(id).next)
  }
}

case class Phase(var id: String, @JsonProperty("class") className: String, next: String, sidecars: List[String], config: Map[String,Any])
