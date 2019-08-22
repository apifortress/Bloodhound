/**
  * Copyright 2019 API Fortress
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
package com.apifortress.afthem.config

import com.apifortress.afthem.UriUtil
import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonProperty}
import javax.servlet.http.HttpServletRequest
import org.slf4j.{Logger, LoggerFactory}

/**
  * Companion object to load backends from file as a singleton
  */
object Backends  {

    /**
      * Logger
      */
    val log : Logger = LoggerFactory.getLogger(classOf[Backends])

    /**
      * The Backends singleton
      * @return the Backends signleton
      */
    def instance() : Backends = {
        return this.synchronized {
           return ConfigLoader.loadBackends()
        }
    }

}

/**
  * Data structure representing the configuration of backends
  */
@JsonIgnoreProperties(ignoreUnknown = true)
class Backends(backends: List[Backend]) extends ICacheableConfig {

    /**
      * Given an inbound request URL, find the Backend definition matching it
      * @param url the inbound request URL
      * @return an instance of Option[Backend]
      */
    def findByUrl(url : String) : Option[Backend] = {
        val signature = UriUtil.getSignature(url)
        return backends.find(bec => signature.startsWith(bec.prefix))
    }

    /**
      * Given an inbound request, find the Backend definition matching it
      * @param request the inbound request
      * @return an instance of Option[Backend]
      */
    def findByRequest(request : HttpServletRequest) : Option[Backend] = {
        val signature = UriUtil.getSignature(request.getRequestURL.toString)
        val backend = backends.find { backend =>
            var found = true
            if(signature.startsWith(backend.prefix)) {
                if(backend.headers != null)
                    for ((k, v) <- backend.headers)
                        if (v != request.getHeader(k))
                            found = false
            }
            else
                found = false
            found
        }
        return backend
    }
}

/**
  * The single backend configuration
  * @param prefix the inbound URI prefix
  * @param upstream the upstream URI
  */
@JsonIgnoreProperties(ignoreUnknown = true)
case class Backend(@JsonProperty("flow_id") flowId: String, prefix: String, headers : Map[String,String], val upstream: String)
