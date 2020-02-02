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
package com.apifortress.afthem.config

import com.fasterxml.jackson.annotation.{JsonInclude, JsonProperty}

/**
  * The root configuration.
  * @param configLoader the configuration loader definition
  */
case class RootConfigConf(@JsonProperty("config_loader") val configLoader : ConfigLoaderConf, val mime : Mime,
                          @JsonInclude(JsonInclude.Include.NON_NULL) @JsonProperty("cluster_id") val clusterId : String = "default")

/**
  * The configuration loader definition
  * @param className the class of the configuration loader
  * @param params params that the configuration loader may require
  */
case class ConfigLoaderConf(@JsonProperty("class") val className : String, val params : Map[String,Any])

/**
  * Configuration for mime types
  * @param textContentTypeContain a content type should contain one of these substrings if it's representing text
  */
case class Mime(@JsonProperty("text_content_types_contain") val textContentTypeContain: List[String])