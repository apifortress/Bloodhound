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
package com.apifortress.afthem.config.loaders

import java.io.File
import java.util.Date

import com.apifortress.afthem.Parsers
import com.apifortress.afthem.config.Backends.log
import com.apifortress.afthem.config._

import scala.io.Source

/**
  * YamlConfigLoader companion object
  */
object YamlConfigLoader {

  /**
    * The subpath to the configuration files. This is alterable for testing purposes.
    */
  var SUBPATH = "etc"
}

/**
  * Default config loader. Loads configuration from the file system, YAML files
  * @param params any configuration parameters. This load needs none
  */
class YamlConfigLoader(params: Map[String,Any] = null) extends TConfigLoader {

  def loadAfthemRootConf() : RootConfigConf = {
    return parse[RootConfigConf]("afthem.yml", classOf[RootConfigConf])
  }

  def loadRestarterFlag() : RestartFlag = {
    if(fileExists("restarter_flag.yml"))
      return parse[RestartFlag]("restarter_flag.yml", classOf[RestartFlag])
    else
      new RestartFlag(-1l,new Date())
  }

  override def loadBackends(): Backends = {
    var instance : Backends = AfthemCache.configCache.get("backends").asInstanceOf[Backends]
    if (instance != null){
      log.debug("Backends loaded from cache")
      return instance
    }
    log.debug("Backends loaded from disk")
    instance = parse[Backends]("backends.yml", classOf[Backends])
    AfthemCache.configCache.put("backends", instance)
    return instance
  }

  override def loadFlow(id: String): Flow = {
    return parse[Flow]("flows"+File.separator+id+".yml",classOf[Flow])
  }

  override def loadImplementers(): Implementers = {
    return parse[Implementers]("implementers.yml",classOf[Implementers])
  }

  private def fileExists(filename : String) : Boolean =
    new File(YamlConfigLoader.SUBPATH+File.separator+filename).exists()

  /**
    * Parses a configuration file in the etc/ directory
    * @param filename the filename we want to load
    * @param theClass the class of the configuration file
    * @tparam T the class of the configuration file
    * @return the parsed configuration file
    */
  private def parse[T](filename : String, theClass : Class[T]): T = {
    val reader = Source.fromFile(YamlConfigLoader.SUBPATH+File.separator+filename).reader()
    val resp : T = Parsers.parseYaml[T](reader, theClass)
    reader.close()
    return resp
  }
}
