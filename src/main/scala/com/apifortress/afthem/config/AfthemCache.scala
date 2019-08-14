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

import org.ehcache.{Cache, CacheManager}
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.xml.XmlConfiguration
import org.springframework.expression.Expression

/**
  * Object managing Afthem caches
  */
object AfthemCache {

  /**
    * The EHCache manager
    */
  val cacheManager : CacheManager = CacheManagerBuilder.newCacheManager(new XmlConfiguration(new File("etc/ehcache.xml").getAbsoluteFile.toURI.toURL))
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
    * Cache dedicated to parsed expressions
    */
  val expressionsCache : Cache[String,Expression] = cacheManager.getCache("expressions",classOf[String],classOf[Expression])

}
