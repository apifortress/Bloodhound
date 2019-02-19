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

package com.apifortress.afthem

import java.io.File

import scala.io.Source

/**
  * Utils for the configuration files
  */
object ConfigUtil {

  /**
    * Parses a configuration file in the etc/ directory
    * @param filename the filename we want to load
    * @param theClass the class of the configuration file
    * @tparam T the class of the configuration file
    * @return the parsed configuration file
    */
  def parse[T](filename : String, theClass : Class[T]): T = {
    val reader = Source.fromFile("etc"+File.separator+filename).reader()
    val resp : T = Parsers.parseYaml[T](reader, theClass)
    reader.close()
    return resp
  }
}
