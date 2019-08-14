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

import com.fasterxml.jackson.annotation.JsonProperty

/**
  * A collection of API keys
  * @param apiKeys a list of ApiKey objects
  */
class ApiKeys(@JsonProperty("api_keys") val apiKeys : List[ApiKey]) {

  def getApiKey(key : String): Option[ApiKey] ={
    if(key == null)
      return Option.empty[ApiKey]
    return apiKeys.find( item => item.apiKey == key)
  }
}

/**
  * An API Key
  * @param apiKey the key itself
  * @param appId  the application ID
  * @param enabled true if the key is enabled
  */
case class ApiKey(@JsonProperty("api_key") apiKey: String, @JsonProperty("app_id") appId: String, enabled : Boolean)
