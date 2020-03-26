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

import java.io.{ByteArrayInputStream, Reader}

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.commons.lang.exception.ExceptionUtils

import scala.xml.{PrettyPrinter, XML}

/**
  * All parsing functions
  */
object Parsers {

  /**
    * The yaml mapper
    */
  private val yamlMapper : ObjectMapper = new ObjectMapper(new YAMLFactory())
  yamlMapper.registerModule(DefaultScalaModule)

  /**
    * The json mapper
    */
  private val jsonMapper : ObjectMapper = new ObjectMapper()
  jsonMapper.registerModule(DefaultScalaModule)

  /**
    * The json mapper with pretty printer
    */
  private val prettyJsonMapper = jsonMapper.writerWithDefaultPrettyPrinter()

  /**
    * The xml mapper
    */
  private val xmlMapper : XmlMapper = new XmlMapper()
  xmlMapper.registerModule(DefaultScalaModule)

  /**
    * The xml mapper with pretty printer
    */
  private val prettyXmlMapper = xmlMapper.writerWithDefaultPrettyPrinter()

  /**
    * Parses a YAML from string to a specific Class
    * @param data the YAML in string format
    * @param theClass the class we want to parse to
    * @tparam T the class we want to parse to
    * @return the YAML parsed into the given class
    */
  def parseYaml[T](data : String,theClass : Class[T]) : T = yamlMapper.readValue(data,theClass)

  /**
    * Parses a YAML from a reader to a specific Class
    * @param data a reader to YAML data
    * @param theClass the class we want to parse to
    * @tparam T the class we want to parse to
    * @return the YAML parsed into the given class
    */
  def parseYaml[T](data : Reader, theClass : Class[T]) : T = yamlMapper.readValue(data,theClass)

  /**
    * Parses a JSON from string to a specific Class
    * @param data the JSON in string format
    * @param theClass the class we want to parse to
    * @tparam T the class we want to parse to
    * @return the JSON parsed into the given class
    */
  def parseJSON[T](data : String,theClass : Class[T]) : T = jsonMapper.readValue(data,theClass)

  /**
    * Parses a JSON from a byte array to a specific Class
    * @param data the JSON in byte array format
    * @param theClass the class we want to parse to
    * @tparam T the class we want to parse to
    * @return the JSON parsed into the given class
    */
  def parseJSON[T](data : Array[Byte],theClass : Class[T]) : T = jsonMapper.readValue(data,theClass)

  /**
    * Parses a JSON from a reader to a specific Class
    * @param data a reader to JSON data
    * @param theClass the class we want to parse to
    * @tparam T the class we want to parse to
    * @return the JSON parsed into the given class
    */
  def parseJSON[T](data : Reader, theClass : Class[T]) : T = jsonMapper.readValue(data,theClass)

  /**
    * Parses an XML from a string to a specific Class
    * @param data the XML in string format
    * @param theClass the class we want to parse to
    * @tparam T the class we want to parse to
    * @return the XML parsed into the given class
    */
  def parseXML[T](data : String,theClass : Class[T]) : T = xmlMapper.readValue(data,theClass)

  /**
    * Parses an XML from a byte array to a specific Class
    * @param data the XML in byte array format
    * @param theClass the class we want to parse to
    * @tparam T the class we want to parse to
    * @return the XML parsed into the given class
    */
  def parseXML[T](data : Array[Byte],theClass : Class[T]) : T = xmlMapper.readValue(data,theClass)

  /**
    * Parses an XML from a reader to a specific Class
    * @param data a reader to XML data
    * @param theClass the class we want to parse to
    * @tparam T the class we want to parse to
    * @return the XML parsed into the given class
    */
  def parseXML[T](data : Reader, theClass : Class[T]) : T = xmlMapper.readValue(data,theClass)

  /**
    * Serializes an object to a JSON string
    * @param data an object to be serialized
    * @param pretty true if pretty print is desired
    * @return the JSON-serialized object as a string
    */
  def serializeAsJsonString(data : Any, pretty : Boolean = true) : String = {
    if (pretty) prettyJsonMapper.writeValueAsString(data)
    else jsonMapper.writeValueAsString(data)
  }

  def serializeAsXmlString(data : Any, rootValue : String) : String = {
    val writer = xmlMapper.writer().withRootName(rootValue)
    return writer.writeValueAsString(data)
  }

  def serializeExceptionAsJSONString(exception : Exception, pretty: Boolean = true) : String = {
    val msg = Map("status" -> "error","message"->ExceptionUtils.getMessage(exception))
    return Parsers.serializeAsJsonString(msg,true)
  }

  def serializeExceptionAsXMLString(exception : Exception) : String = {
    val msg = Map("status" -> "error","message"->ExceptionUtils.getMessage(exception))
    return Parsers.serializeAsXmlString(msg,"exception")
  }

  /**
    * Serializes an object to a JSON byte array
    * @param data an object to be serialized
    * @param pretty true if pretty print is desired
    * @return the JSON-serialized object as byte array
    */
  def serializeAsJsonByteArray(data : Any, pretty : Boolean = true) : Array[Byte] = {
    if (pretty) prettyJsonMapper.writeValueAsBytes(data)
    else jsonMapper.writeValueAsBytes(data)
  }

  /**
    * Beautifies a JSON stored into a byte array and returns a byte array
    * @param data a JSON stored into a byte array
    * @return a byte array containing the beautified JSON
    */
  def beautifyJSON(data : Array[Byte]) : Array[Byte] = {
    return serializeAsJsonByteArray(parseJSON[Object](data, classOf[Object]))
  }

  /**
    * Beautifies an XML stored into a byte array and returns a byte array
    * @param data a XML stored into a byte array
    * @return a byte array containing the beautified XML
    */
  def beautifyXML(data : Array[Byte]) : Array[Byte] = {
    val p = new PrettyPrinter(80,4)
    val is = new ByteArrayInputStream(data)
    val xml = XML.load(is)
    is.close()
    return p.format(xml).getBytes
  }


}
