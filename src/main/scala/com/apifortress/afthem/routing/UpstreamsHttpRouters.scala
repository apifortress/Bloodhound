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
import org.slf4j.{Logger, LoggerFactory}

/**
  * The object dealing with the Upstreams HTTP routers
  */
object UpstreamsHttpRouters {

  private def log : Logger = LoggerFactory.getLogger(UpstreamsHttpRouters.getClass)

  /**
    * Given a Backend instance, it either returns a previously cached router, or creates a new one for it
    * @param backend a Backend instance
    * @return a router
    */
  def getRouter(backend : Backend) : TUpstreamHttpRouter = {
    /*
     * We look for an existing router based on the backend signature. The signature only includes
     * the inbound filters, such as the prefix and the headers.
     */
    val routerOption = AfthemCache.routersCache.get(backend.getSignature())
    if(routerOption != null) {
      log.debug("Router in cache")
      /*
       * If the hashcode of the backend is different from the hashcode stored in the router, it means
       * that something meaningful has changed in the backend.
       */
      if (routerOption.getBackendHashCode() != backend.hashCode) {
        log.debug("Existing router has updated upstreams. Updating")
        routerOption.update(backend)
      }
      return routerOption
    }
    /*
     * Creating a new router serving the provided backend
     */
    log.debug("Creating new router")
    val router = new UpstreamsRoundRobinHttpRouter(backend)
    AfthemCache.routersCache.put(backend.getSignature(),router)
    return router
  }

  /**
    * Given a Backend instance, it creates a router or retrieves an existing one, and determines what's the next
    * upstream url. If the backend just have the single upstream, that is returned.
    * @param backend a Backend instance
    * @return an upstream URL
    */
  def getUrl(backend : Backend) : String = {
    /*
     * If upstream is present, it means no routing is necessary and we can return that
     */
    if(backend.upstream != null)
      return backend.upstream
    /*
     * Otherwise, we ask the routing system which upstream needs to be used
     */
    if(backend.upstreams == null || backend.upstreams.urls.size == 0)
      return null
    return getRouter(backend).getNextUrl()
  }

}

