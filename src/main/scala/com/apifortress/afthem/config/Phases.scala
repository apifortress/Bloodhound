package com.apifortress.afthem.config

import java.io.{File, InputStreamReader}

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.io.Source

object Phases {

  val objectMapper: ObjectMapper = new ObjectMapper(new YAMLFactory())
  objectMapper.registerModule(DefaultScalaModule)

  var phasesInstance : Phases = null

  def load(): Phases = {
    if(phasesInstance != null)
      return phasesInstance

    phasesInstance = parse(Source.fromFile("etc"+File.separator+"phases.yml").reader())
    return phasesInstance
  }

  def parse(data : InputStreamReader): Phases = {
    return objectMapper.readValue(data, classOf[Phases])
  }
}
class Phases {

  var phases : Map[String,Phase] = null

  def getPhase(id: String) : Phase = {
    val phase = phases.get(id).get
    phase.id = id
    return phase
  }
  def getNextPhase(id: String) : Phase = {
    return getPhase(getPhase(id).next)
  }
}
