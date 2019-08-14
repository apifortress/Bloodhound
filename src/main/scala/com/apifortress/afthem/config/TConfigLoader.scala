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

/**
  * Trait for config loaders
  */
trait TConfigLoader {

  /**
    * Loads backend definitions
    * @return an instance of Backends
    */
  def loadBackends() : Backends

  /**
    * Loads a flow definition
    * @param id the ID of the flow
    * @return an instance of Flow
    */
  def loadFlow(id : String) : Flow

  /**
    * Loads implementers
    * @return an instance of Implementers
    */
  def loadImplementers() :  Implementers

}
