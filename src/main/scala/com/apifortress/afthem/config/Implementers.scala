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

import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonProperty}

/**
  * Companion class to obtain singleton Implementers instance
  */
object Implementers {

  /**
    * The Implementers singletoon
    */
  val instance: Implementers = ConfigLoader.loadImplementers()

}

/**
  * A class to store the implements configuration file
  * @param implementers the list of implementers
  */
@JsonIgnoreProperties(ignoreUnknown = true)
case class Implementers(implementers: List[Implementer],
                        @JsonProperty("thread_pools") threadPools : Map[String,ThreadPool])

/**
  * The single implementer class
  * @param id the ID of the implementer
  * @param className the class name of the implementer
  * @param threadPool the threadPool assigned to the implementer, if any
  */
@JsonIgnoreProperties(ignoreUnknown = true)
case class Implementer(id : String, @JsonProperty("class") className : String,
                       @JsonProperty("type") actorType : String, instances: Int = 1,
                      @JsonProperty("thread_pool") threadPool : String)

/**
  * The thread pool definition class
  * @param min the minimum number of threads
  * @param max the maximum number of threads
  * @param factor the CPU factors
  */
@JsonIgnoreProperties(ignoreUnknown = true)
case class ThreadPool(min: Int, max: Int, factor: Int)