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

import java.io.Reader

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object Parsers {

  private val yamlMapper : ObjectMapper = new ObjectMapper(new YAMLFactory())
  yamlMapper.registerModule(DefaultScalaModule)

  private val jsonMapper : ObjectMapper = new ObjectMapper()
  jsonMapper.registerModule(DefaultScalaModule)
  private val prettyJsonMapper = jsonMapper.writerWithDefaultPrettyPrinter()

  private val xmlMapper : XmlMapper = new XmlMapper()
  xmlMapper.registerModule(DefaultScalaModule)
  private val prettyXmlMapper = xmlMapper.writerWithDefaultPrettyPrinter()

  def parseYaml[T](data : String,theClass : Class[T]) : T = yamlMapper.readValue(data,theClass)

  def parseYaml[T](data : Reader, theClass : Class[T]) : T = yamlMapper.readValue(data,theClass)

  def parseJSON[T](data : String,theClass : Class[T]) : T = jsonMapper.readValue(data,theClass)

  def parseJSON[T](data : Array[Byte],theClass : Class[T]) : T = jsonMapper.readValue(data,theClass)

  def parseJSON[T](data : Reader, theClass : Class[T]) : T = jsonMapper.readValue(data,theClass)

  def parseXML[T](data : String,theClass : Class[T]) : T = xmlMapper.readValue(data,theClass)

  def parseXML[T](data : Array[Byte],theClass : Class[T]) : T = xmlMapper.readValue(data,theClass)

  def parseXML[T](data : Reader, theClass : Class[T]) : T = xmlMapper.readValue(data,theClass)


  def deserializeAsJsonString(data : Any, pretty : Boolean = true) : String = {
    if (pretty) prettyJsonMapper.writeValueAsString(data)
    else jsonMapper.writeValueAsString(data)
  }

  def deserializeAsJsonByteArray(data : Any, pretty : Boolean = true) : Array[Byte] = {
    if (pretty) prettyJsonMapper.writeValueAsBytes(data)
    else jsonMapper.writeValueAsBytes(data)
  }

  def deserializeAsXmlString(data : Any, pretty : Boolean = true) : String = {
    if (pretty) prettyXmlMapper.writeValueAsString(data)
    else xmlMapper.writeValueAsString(data)
  }

  def deserializeAsXmlByteArray(data : Any, pretty : Boolean = true) : Array[Byte] = {
    if(pretty) prettyXmlMapper.writeValueAsBytes(data)
    else xmlMapper.writeValueAsBytes(data)
  }

  def beautifyJSON(data : Array[Byte]) : Array[Byte] = {
    return deserializeAsJsonByteArray(parseJSON[Object](data, classOf[Object]))
  }

  def beautifyXML(data : Array[Byte]) : Array[Byte] = {
    return deserializeAsXmlByteArray(parseXML[Object](data, classOf[Object]))
  }


}
