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

import com.apifortress.afthem.config.{AfthemCache, Backend}

/**
  * The object dealing with the Upstreams HTTP routers
  */
object UpstreamsHttpRouters {

  /**
    * Given a Backend instance, it either returns a previously cached router, or creates a new one for it
    * @param backend a Backend instance
    * @return a router
    */
  def getRouter(backend : Backend) : TUpstreamHttpRouter = {
    val routerOption = AfthemCache.routersCache.get(backend.hashCode)
    if(routerOption != null)
      return routerOption
    val router = new UpstreamsRoundRobinHttpRouter(backend)
    AfthemCache.routersCache.put(backend.hashCode,router)
    return router
  }

  /**
    * Given a Backend instance, it creates a router or retrieves an existing one, and determines what's the next
    * upstream url. If the backend just have the single upstream, that is returned.
    * @param backend a Backend instance
    * @return an upstream URL
    */
  def getUrl(backend : Backend) : String = {
    if(backend.upstream != null)
      return backend.upstream
    if(backend.upstreams == null || backend.upstreams.urls.size == 0)
      return null
    return getRouter(backend).getNextUrl()
  }

}

