package com.apifortress.afthem.config

import com.fasterxml.jackson.annotation.JsonProperty

case class Phase(var id: String, @JsonProperty("class") className: String, next: String, sidecars: List[String], config: Map[String,Any], instances : Int = 1, dispatcher : String = null)
