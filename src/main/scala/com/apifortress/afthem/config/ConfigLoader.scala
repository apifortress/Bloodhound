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

import com.apifortress.afthem.config.loaders.YamlConfigLoader

/**
  * Object loading the configuration using the appropriate implementation
  */
object ConfigLoader extends TConfigLoader {

  /**
    * The root configuration
    */
  private val rootConfig = new YamlConfigLoader().loadAfthemRootConf()

  /**
    * The implementation for all configurations except root
    */
  val implementer : TConfigLoader = Class.forName(rootConfig.configLoader.className)
                                      .getDeclaredConstructor(classOf[Map[String,Any]])
                                      .newInstance(rootConfig.configLoader.params).asInstanceOf[TConfigLoader]


  override def loadBackends(): Backends = return implementer.loadBackends()

  override def loadFlow(id: String): Flow = return implementer.loadFlow(id)

  override def loadImplementers(): Implementers = return implementer.loadImplementers()
}
