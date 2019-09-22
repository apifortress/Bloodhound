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
package com.apifortress.afthem.routing

import com.apifortress.afthem.config.Backend

/**
  * A round robin upstream http router
  * @param backend a Backend instance
  */
class UpstreamsRoundRobinHttpRouter(val backend : Backend) extends TUpstreamHttpRouter {

  /**
    * The index in the URLs list
    */
  private var index : Int = 0

  /**
    * The upstream URLs
    */
  private var urls = backend.upstreams.urls

  private var backendHash = backend.hashCode

  private val probe = backend.upstreams.probe

  if(probe != null){

  }

  def getNextUrl() : String = synchronized {
    index = ((index+1) % urls.size)
    return urls(index)
  }

  override def getBackendHashCode(): Int = backendHash

  def update(backend: Backend) : Unit = {
    this.urls = backend.upstreams.urls
    this.backendHash = backend.hashCode
  }

}