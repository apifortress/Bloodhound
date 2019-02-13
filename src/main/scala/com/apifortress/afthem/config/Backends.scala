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
