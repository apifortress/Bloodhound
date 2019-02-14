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

import com.apifortress.afthem.actors.transformers.BeautifyPayloadActor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.commons.codec.binary.StringUtils

object Parsers {

  private val yamlMapper : ObjectMapper = new ObjectMapper(new YAMLFactory())
  yamlMapper.registerModule(DefaultScalaModule)

  private val jsonMapper : ObjectMapper = new ObjectMapper()
  jsonMapper.registerModule(DefaultScalaModule)

  private val xmlMapper : XmlMapper = new XmlMapper()
  xmlMapper.registerModule(DefaultScalaModule)

  def parseYaml[T](data : String,theClass : Class[T]) : T = yamlMapper.readValue(data,theClass)

  def parseYaml[T](data : Reader, theClass : Class[T]) : T = yamlMapper.readValue(data,theClass)

  def parseJSON[T](data : String,theClass : Class[T]) : T = jsonMapper.readValue(data,theClass)

  def parseJSON[T](data : Array[Byte],theClass : Class[T]) : T = jsonMapper.readValue(data,theClass)

  def parseJSON[T](data : Reader, theClass : Class[T]) : T = jsonMapper.readValue(data,theClass)

  def parseXML[T](data : String,theClass : Class[T]) : T = xmlMapper.readValue(data,theClass)

  def parseXML[T](data : Array[Byte],theClass : Class[T]) : T = xmlMapper.readValue(data,theClass)

  def parseXML[T](data : Reader, theClass : Class[T]) : T = xmlMapper.readValue(data,theClass)

  def deserializeAsPrettyJsonByteArray(data : Any) : Array[Byte] = {
    return StringUtils.getBytesUtf8(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data))
  }

  def deserializeAsPrettyXmlByteArray(data : Any) : Array[Byte] = {
    return StringUtils.getBytesUtf8(xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data))
  }

  def beautifyJSON(data : Array[Byte]) : Array[Byte] = {
    return deserializeAsPrettyJsonByteArray(parseJSON[Object](data, classOf[Object]))
  }

  def beautifyXML(data : Array[Byte]) : Array[Byte] = {
    return deserializeAsPrettyXmlByteArray(parseXML[Object](data, classOf[Object]))
  }


}
