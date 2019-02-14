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

import java.io.File

import com.apifortress.afthem.ConfigUtil
import com.apifortress.afthem.ConfigUtil.objectMapper
import com.apifortress.afthem.config.Flow
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.collection.mutable
import scala.io.Source

/**
  * Companion object to load phases from file as a singleton
  */
object Flows {

  private val objectMapper: ObjectMapper = new ObjectMapper(new YAMLFactory())
  objectMapper.registerModule(DefaultScalaModule)

  val instance : Flows = new Flows()

}

class Flows extends mutable.LinkedHashMap[String,Flow] {

  def getFlow(flowId : String) : Flow = {
    val flow = get(flowId)
    if(flow.isDefined)
      return flow.get
    else
      loadFlow(flowId)
  }

  private def loadFlow(flowId : String) : Flow = {
    val flow = ConfigUtil.parse[Flow]("flows"+File.separator+flowId+".yml",classOf[Flow])
    put(flowId,flow)
    return flow
  }


}

class Flow extends java.util.HashMap[String,Phase] {


  def getPhase(id: String) : Phase = {
    val phase = get(id)
    phase.id = id
    return phase
  }

  def getNextPhase(id: String) : Phase = {
    return getPhase(getPhase(id).next)
  }

}

class Phase(var id: String, @JsonProperty("class") val className: String, val next: String, val sidecars: List[String], val config: Map[String,Any]){

  def getConfig() : Map[String,Any] = {
    return if (config == null) return Map.empty[String,Any] else config
  }

  def getConfigString(key : String) : String = {
    val data = getConfig.get(key)
    return if (data.isDefined) data.get.asInstanceOf[String] else null
  }

  def getConfigList(key : String) : List[String] = {
    val data = getConfig.get(key)
    return if (data.isDefined) data.get.asInstanceOf[List[String]] else List.empty[String]
  }
}
