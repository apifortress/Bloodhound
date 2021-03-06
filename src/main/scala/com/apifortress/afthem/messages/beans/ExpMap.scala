/**
  * Copyright 2020 API Fortress
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
package com.apifortress.afthem.messages.beans

import java.util.UUID

import scala.collection.mutable

/**
  * Companion object for ExpMap
  */
object ExpMap{

  def apply: ExpMap = new ExpMap()

  def apply(tuple : Tuple2[String,Any]) : ExpMap = {
    val map = new ExpMap
    map.put(tuple._1,tuple._2)
    return map
  }

}

/**
  * A mutable map that exposes extra methods to simplify the integration with SpEL
  */
class ExpMap extends mutable.HashMap[String,Any] {

  put("__id",UUID.randomUUID().toString)

  /**
    * Retrieves a value based on key. If the key is absent in the map, defaultValue is returned
    * @param key the key
    * @param defaultValue the default value
    * @return the found value
    */
  def getOrElse(key : String, defaultValue : Any) : Any = super.getOrElse(key,defaultValue)

}
