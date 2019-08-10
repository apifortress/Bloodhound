package com.apifortress.afthem.config

import com.fasterxml.jackson.annotation.JsonProperty

case class RootConfigConf(@JsonProperty("config_loader") configLoader : ConfigLoaderConf)

case class ConfigLoaderConf(@JsonProperty("class") className : String, params: Map[String,Any])