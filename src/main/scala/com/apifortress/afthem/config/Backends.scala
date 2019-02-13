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

import java.io.{File, InputStreamReader}

import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonProperty}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.io.Source

object Backends {

  val objectMapper: ObjectMapper = new ObjectMapper(new YAMLFactory())
  objectMapper.registerModule(DefaultScalaModule)

  var backendsInstance: Backends = null

  def load(): Backends = {
    if(backendsInstance != null)
      return backendsInstance

    backendsInstance = parse(Source.fromFile("etc"+File.separator+"backends.yml").reader())
    return backendsInstance
  }

  def parse(data : InputStreamReader): Backends = {
    return objectMapper.readValue(data, classOf[Backends])
  }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Backends {

  @JsonProperty("backends")
  var backends : List[Backend] = null

}

case class Backend(prefix: String, upstream: String)
