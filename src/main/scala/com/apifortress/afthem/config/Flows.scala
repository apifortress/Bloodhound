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
import com.fasterxml.jackson.annotation.JsonProperty

import scala.collection.mutable

/**
  * Companion object to create a single instance of Flows
  */
object Flows {

  val instance : Flows = new Flows()

}

/**
  * Flows will load from disk, parse and retain in memory all flows that get requested
  */
class Flows extends mutable.LinkedHashMap[String,Flow] {

  /**
    * If the flow has already been loaded and parsed, that gets returned.
    * Otherwise, the flow get loaded from file, stored and returned
    * @param flowId the ID of the flow to retrieve
    * @return the parsed flow
    */
  def getFlow(flowId : String) : Flow = {
    val flow = get(flowId)
    if(flow.isDefined)
      return flow.get
    else
      loadFlow(flowId)
  }

  /**
    * Loads a flow from disk, parses it, stores it, and returns it
    * @param flowId the ID of the flow to load
    * @return the loaded flow
    */
  private def loadFlow(flowId : String) : Flow = {
    val flow = ConfigUtil.parse[Flow]("flows"+File.separator+flowId+".yml",classOf[Flow])
    put(flowId,flow)
    return flow
  }

}

/**
  * A Flow is a sequence of Phases, representing the events happening to a request
  */
class Flow extends java.util.HashMap[String,Phase] {

  /**
    * Returns a phase by the ID
    * @param id the ID of the phase to return
    * @return the requested phase
    */
  def getPhase(id: String) : Phase = {
    val phase = get(id)
    phase.id = id
    return phase
  }

  /**
    * Given the ID of a phase, it load the next planned phase, if any
    * @param id the ID of the a phase
    * @return the next phase
    */
  def getNextPhase(id: String) : Phase = {
    return getPhase(getPhase(id).next)
  }

}

/**
  * A Phase describes what should happen to a request as it goes through it
  * @param id the ID of the phase
  * @param next the ID of the next phase, if any
  * @param sidecars a list of the IDs of the sidecar phases
  * @param config the configuration object, if any
  */
class Phase(var id: String, val next: String, val sidecars: List[String], val config: Map[String,Any]){

  /**
    * Returns the config object
    * @return the config object
    */
  def getConfig() : Map[String,Any] = {
    return if (config == null) return Map.empty[String,Any] else config
  }

  /**
    * Retrieves a configuration item as string
    * @param key the key of the configuration item
    * @return the configuration item as string or null
    */
  def getConfigString(key : String) : String = {
    val data = getConfig.get(key)
    return if (data.isDefined) data.get.asInstanceOf[String] else null
  }

  /**
    * Retrieves a configuration item as a list
    * @param key the key of the configuration item
    * @return the configuration item as a list
    */
  def getConfigList(key : String) : List[String] = {
    val data = getConfig.get(key)
    return if (data.isDefined) data.get.asInstanceOf[List[String]] else List.empty[String]
  }

  /**
    * Retrieves a configuration item as a boolean
    * @param key the key of the configuration item
    * @return the configuration item as an Option[Boolean] as you may need to know whether the
    *         configuration item is at all
    */
  def getConfigBoolean(key : String) : Option[Boolean] = {
    return getConfig().get(key).asInstanceOf[Option[Boolean]]
  }
}