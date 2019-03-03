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
    * Given a URL and a backend object, it determines the part of the path that belongs to the upstream
    * @param url the URL
    * @param backend the backend configuration object
    * @return the part of the path belonging to the upstream
    */
  def determineUpstreamPart(url: String, backend: Backend): String = {
    var subPath = url.substring(url.indexOf(backend.prefix)+backend.prefix.length)
    var upstreamUrl = backend.upstream
    if(!upstreamUrl.endsWith("/"))
      upstreamUrl+='/'
    if(subPath.startsWith("/"))
      subPath = subPath.substring(1)
    return subPath
  }

  /**
    * Given the URL of an incoming request, and a matching backend configuration,
    * it obtains the upstream URL to hit
    * @param url incoming request URL
    * @param backend the backend configuration
    * @return the upstream URL
    */
  def determineUpstreamUrl(url: String, backend: Backend): String = {
    return backend.upstream+determineUpstreamPart(url,backend)
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

  /**
    * Transforms the URI that is compatible with the expectations of the API Fortress serializer
    * @param uri the URI to be transformed
    * @return the transformed URI
    */
  def toSerializerUri(uri : String): String = {
    val url = new URL(uri)
    return url.getPath+(if (url.getQuery!=null) "?"+url.getQuery else "")
  }
}
