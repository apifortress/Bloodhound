package com.apifortress.afthem.config

import com.fasterxml.jackson.annotation.JsonProperty


object ApiKeys {

}

class ApiKeys(@JsonProperty("api_keys") val apiKeys : List[ApiKey]) {

  def getApiKey(key : String): Option[ApiKey] ={
    if(key == null)
      return Option.empty[ApiKey]
    return apiKeys.find( item => item.apiKey == key)
  }
}

case class ApiKey(@JsonProperty("api_key") apiKey: String, @JsonProperty("app_id") appId: String, enabled : Boolean)
