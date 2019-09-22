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

import java.util.Objects

import com.apifortress.afthem.UriUtil
import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonProperty}
import javax.servlet.http.HttpServletRequest
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration.Duration

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
class Backends(val backends: List[Backend]) extends ICacheableConfig {

    /**
      * Given an inbound request, find the Backend definition matching it
      * @param request the inbound request
      * @return an instance of Option[Backend]
      */
    def findByRequest(request : HttpServletRequest) : Option[Backend] = {
        val signature = UriUtil.getSignature(request.getRequestURL.toString)
        val backend = backends.find { backend =>
            var found = true
            if(signature.matches(backend.prefix+".*")) {
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

    def list() : List[Backend] = return backends
}

/**
  * The single backend configuration
  * @param flowId the ID of the flow
  * @param prefix the inbound URI prefix
  * @param upstream the upstream URI
  * @param upstreams in case multiple upstreams are described in this backend. An instance of the Upstreams objects
  * @param meta meta information to be added to the message meta
  *
  */
@JsonIgnoreProperties(ignoreUnknown = true)
class Backend(@JsonProperty("flow_id") val flowId: String, val prefix: String, val headers : Map[String,String],
                   val upstream: String, val upstreams : Upstreams = null, val meta : Map[String,Any] = Map.empty[String,Any]){

    override def hashCode : Int = {
        return Objects.hash(flowId,prefix,headers,upstream,upstreams)
    }

    /**
      * A signature is a hash working as an ID of the backend. It hashes the prefix and the headers which are the
      * inbound filters. Ideally they should be unique within Backends.
      * @return the hash
      */
    def getSignature() : Int = {
        return Objects.hash(prefix,headers)
    }
}

/**
  * An object representing multiple upstreams
  * @param urls a list of upstream URLs
  */
class Upstreams(val urls : List[String], val probe : Probe = null) {

    override def hashCode : Int = {
        return Objects.hash(urls)
    }

}

class Probe(val path : String, val status: Int, val method : String, private val timeout: String,
            private val interval : String, @JsonProperty("count_down") val countDown : Int,
            @JsonProperty("count_up") val countUp : Int) {

    val timeoutDuration = Duration.create(timeout)

    val intervalDuration = Duration(interval)


}