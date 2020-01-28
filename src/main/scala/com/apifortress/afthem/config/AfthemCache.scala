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

import java.io.File

import com.apifortress.afthem.config.loaders.YamlConfigLoader
import com.apifortress.afthem.routing.TUpstreamHttpRouter
import com.google.common.util.concurrent.RateLimiter
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.event._
import org.ehcache.xml.XmlConfiguration
import org.ehcache.{Cache, CacheManager}
import org.slf4j.LoggerFactory
import org.springframework.expression.Expression

/**
  * Object managing Afthem caches
  */
object AfthemCache {

  /**
    * Logger
    */
  val log = LoggerFactory.getLogger(AfthemCache.getClass)

  /**
    * The EHCache manager
    */
  val cacheManager : CacheManager = CacheManagerBuilder.newCacheManager(new XmlConfiguration(new File(YamlConfigLoader.SUBPATH+File.separator+"ehcache.xml").getAbsoluteFile.toURI.toURL))

  cacheManager.init()

  /**
    * Cache dedicated to reloadable configuration files
    */
  val configCache : Cache[String,ICacheableConfig] = cacheManager.getCache("configs",classOf[String],classOf[ICacheableConfig])

  /**
    * Cache for API keys
    */
  val apiKeysCache : Cache[String,ApiKeys] = cacheManager.getCache("api_keys",classOf[String],classOf[ApiKeys])

  /**
    * Cache for httpasswd files
    */
  val htpasswdsCache : Cache[String,String] = cacheManager.getCache("htpasswds",classOf[String],classOf[String])

  /**
    * Cache dedicated to parsed expressions
    */
  val expressionsCache : Cache[String,Expression] = cacheManager.getCache("expressions",classOf[String],classOf[Expression])

  /**
    * Cache for routers
    */
  val routersCache : Cache[Integer,TUpstreamHttpRouter] = cacheManager.getCache("http_routers",classOf[Integer],classOf[TUpstreamHttpRouter])

  // We want to trigger an action when a cache is discovered as expired
  routersCache.getRuntimeConfiguration.registerCacheEventListener(new RoutersCacheListener(),EventOrdering.ORDERED, EventFiring.SYNCHRONOUS, EventType.EXPIRED)


  /**
    * Cache for rate limiters
    */
  val rateLimiterCache : Cache[String,RateLimiter] = cacheManager.getCache("rate_limiter",classOf[String],classOf[RateLimiter])

  /**
    * Clears all caches
    */
  def clearAll() : Unit = {
    configCache.clear()
    apiKeysCache.clear()
    htpasswdsCache.clear()
    expressionsCache.clear()
    routersCache.clear()
  }

}

/**
  * Listener for expired items. We to cancel probes when a router has expired
  */
class RoutersCacheListener extends CacheEventListener[Integer,TUpstreamHttpRouter] {
  override def onEvent(cacheEvent: CacheEvent[_ <: Integer, _ <: TUpstreamHttpRouter]): Unit = {
    AfthemCache.log.debug("Cancelling probe due to Router cache expiry")
    cacheEvent.getOldValue.cancelProbe()
  }
}