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
import com.apifortress.afthem.messages.beans.HttpWrapper
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

    def findByWrapper(requestWrapper : HttpWrapper) : Option[Backend] = {
        val signature = UriUtil.getSignature(requestWrapper.getURL())
        val backend = backends.find { backend =>
            var found = true
            if(signature.matches(backend.prefix+".*")) {
                if(backend.headers != null)
                    for ((k, v) <- backend.headers)
                        if (v != requestWrapper.getHeader(k))
                            found = false
            }
            else
                found = false
            found
        }
        return backend
    }

    /**
      * Returns the List of backends
      * @return the backends collection
      */
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
        return Objects.hash(urls,probe)
    }

}

/**
  * The Probe configuration. Describes how a backend with multiple Upstreams should test the availability of
  * each upstream
  * @param path an extra path fragment to append to the upstream URL for probing. If empty, the upstream URL will be used
  * @param status the expected status code
  * @param method the method to use to perform the request
  * @param timeout how long to wait for the reply before calling it a failure
  * @param interval how frequently should the probe execute
  * @param countDown how many times a probe should fail before the upstream is to be considered down
  * @param countUp how many times a probe should succeed before the upstream is to be considered up
  */
class Probe(val path : String, val status: Int, val method : String, val timeout: String,
            val interval : String, @JsonProperty("count_down") val countDown : Int,
            @JsonProperty("count_up") val countUp : Int) {

    override def hashCode(): Int = {
        return Objects.hash(path,status.toString,method,timeout,interval,countDown.toString,countUp.toString)
    }


}