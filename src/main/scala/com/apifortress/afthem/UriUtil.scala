package com.apifortress.afthem

import com.apifortress.afthem.config.Backend

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
object UriUtil {


  def getSignature(uri: String): String = {
    if(uri.startsWith("http"))
      return uri.substring(uri.indexOf("//")+2)
    return uri
  }

  def determineUpstreamUrl(url: String, backend: Backend): String = {
    val subPath = url.substring(url.indexOf(backend.prefix)+backend.prefix.length)
    return backend.upstream+subPath
  }

  def composeUriAndQuery(uri: String, queryString: String): String = {
    if(queryString != null && !queryString.isEmpty)
      return uri+"?"+queryString
    return uri
  }
}
