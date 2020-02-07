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

import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonInclude, JsonProperty}

/**
  * Companion class to obtain singleton Implementers instance
  */
object Implementers {

  /**
    * The Implementers singletoon
    */
  var instance: Implementers = null

  load()

  /**
    * Loads implementers
    */
  def load(): Unit = {
    instance = ConfigLoader.loadImplementers()
  }

}

/**
  * A class to store the implements configuration file
  * @param implementers the list of implementers
  */
@JsonIgnoreProperties(ignoreUnknown = true)
case class Implementers(val implementers: List[Implementer],
                        @JsonProperty("thread_pools") val threadPools : Map[String,ThreadPool],
                        @JsonInclude(JsonInclude.Include.NON_NULL) val ingresses : List[Ingress] = List.empty[Ingress])

/**
  * The single implementer class
  * @param id the ID of the implementer
  * @param className the class name of the implementer
  * @param threadPool the threadPool assigned to the implementer, if any
  */
@JsonIgnoreProperties(ignoreUnknown = true)
case class Implementer(val id : String, @JsonProperty("class") val className : String,
                       @JsonProperty("type") val actorType : String, val instances : Int = 1,
                      @JsonProperty("thread_pool") val threadPool : String)

/**
  * The thread pool definition class
  * @param min the minimum number of threads
  * @param max the maximum number of threads
  * @param factor the CPU factors
  */
@JsonIgnoreProperties(ignoreUnknown = true)
case class ThreadPool(val min: Int, val max: Int, val factor: Int)

@JsonIgnoreProperties(ignoreUnknown = true)
class Ingress(@JsonProperty("class") val className : String, id: String, sidecars: List[String] = List.empty[String],
              config: Map[String,Any] = Map.empty[String,Any], @JsonProperty("thread_pool") val threadPool : String)
              extends Phase (id, "proxy/request", sidecars, config)
