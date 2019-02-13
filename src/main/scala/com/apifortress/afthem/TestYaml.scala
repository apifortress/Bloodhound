package com.apifortress.afthem

import java.io.File

import com.apifortress.afthem.config.{Backend, Backends}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.io.Source

object TestYaml {

  def main(args: Array[String]): Unit = {
    val mapper : ObjectMapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)
    val data = mapper.readValue(Source.fromFile("etc"+File.separator+"backends.yml").reader(),classOf[Backends])

  }
}
