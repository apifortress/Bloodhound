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

import com.apifortress.afthem.config.Backend
import org.springframework.web.util.{UriComponents, UriComponentsBuilder}


/**
  *
  * Utils to handle URIs and URLs
  */
object UriUtil {


  /**
    * Given a Uri, it generates a signature off that. A signature is a URI without protocol and
    * query string
    *
    * @param uri a uri
    * @return the signature
    */
  def getSignature(uri : String): String = {
    val builder = toUriComponents(uri)
    return builder.getHost+builder.getPath
  }

  /**
    * Given a URL and a backend object, it determines the part of the path that belongs to the upstream
    *
    * @param url the URL
    * @param backend the backend configuration object
    * @return the part of the path belonging to the upstream
    */
  def determineUpstreamPart(uriComponents: UriComponents, backend: Backend): String = {
    val sanitizedUrl = uriComponents.getHost+uriComponents.getPath+(if (uriComponents.getQuery != null) "?"+uriComponents.getQuery else "")
    return sanitizedUrl.replaceFirst(backend.prefix,"")
  }

  /**
    * Given the URL of an incoming request, and a matching backend configuration,
    * it obtains the upstream URL to hit
    *
    * @param url incoming request URL
    * @param backend the backend configuration
    * @return the upstream URL
    */
  def determineUpstreamUrl(uriComponents: UriComponents, upstream : String, backend: Backend): String = {
    return upstream+determineUpstreamPart(uriComponents, backend)
  }

  /**
    * Combines an URI and a query string
    *
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
    *
    * @param uri the URI to be transformed
    * @return the transformed URI
    */
  def toSerializerUri(uri : UriComponents): String = {
    return uri.getPath+(if (uri.getQuery!=null) "?"+uri.getQuery else "")
  }

  /**
    * Makes a URI Builder off a URI
    * @param uri a URI
    * @return a URI Builder
    */
  def toUriComponents(uri : String): UriComponents = {
    return UriComponentsBuilder.fromUriString(uri).build()
  }

  def replacePort(uri : String, port : Int) : String = {
    val components = toUriComponents(uri)
    val builder = new StringBuilder
    builder.append(components.getScheme+"://").append(components.getHost)
    if(port != 80 && port != 443 && port != -1)
      builder.append(":").append(port)
    builder.append(components.getPath)
    if(components.getQuery != null && components.getQuery.length>0)
      builder.append("?").append(components.getQuery)
    return builder.toString()
  }

  def secure(uri : String) : String = uri.replace("http://","https://")

}
