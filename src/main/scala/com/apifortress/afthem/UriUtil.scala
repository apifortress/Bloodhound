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
package com.apifortress.afthem

import java.net.URL

import com.apifortress.afthem.config.Backend



/**
  *
  * Utils to handle URIs and URLs
  */
object UriUtil {


  /**
    * Given a Uri, it generates a signature off that. A signature is a URI without protocol and
    * query string
    * @param uri a uri
    * @return the signagure
    */
  def getSignature(uri: String): String = {
    if(uri.startsWith("http"))
      return uri.substring(uri.indexOf("//")+2)
    return uri
  }

  /**
    * Given the URL of an incoming request, and a matching backend configuration,
    * it obtains the upstream URL to hit
    * @param url incoming request URL
    * @param backend the backend configuration
    * @return the upstream URL
    */
  def determineUpstreamUrl(url: String, backend: Backend): String = {
    var subPath = url.substring(url.indexOf(backend.prefix)+backend.prefix.length)
    var upstreamUrl = backend.upstream
    if(!upstreamUrl.endsWith("/"))
      upstreamUrl+='/'
    if(subPath.startsWith("/"))
      subPath = subPath.substring(1)
    return backend.upstream+subPath
  }

  /**
    * Combines an URI and a query string
    * @param uri a URI
    * @param queryString a query string
    * @return URI and query string combined
    */
  def composeUriAndQuery(uri: String, queryString: String): String = {
    if(queryString != null && !queryString.isEmpty)
      return uri+"?"+queryString
    return uri
  }

  def toSerializerUri(uri : String): String = {
    val url = new URL(uri)
    return url.getPath+(if (url.getQuery!=null) "?"+url.getQuery)
  }
}
