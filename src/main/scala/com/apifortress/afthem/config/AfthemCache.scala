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

object AfthemCache {

  val cacheManager : CacheManager = CacheManagerBuilder.newCacheManager(new XmlConfiguration(new File("etc/ehcache.xml").getAbsoluteFile.toURI.toURL))
  cacheManager.init()

  val configCache : Cache[String,ICachableConfig] = cacheManager.getCache("configs",classOf[String],classOf[ICachableConfig])

  val expressionsCache : Cache[String,Expression] = cacheManager.getCache("expressions",classOf[String],classOf[Expression])

}
