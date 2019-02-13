package com.apifortress.afthem

import com.apifortress.afthem.config.Backend

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
