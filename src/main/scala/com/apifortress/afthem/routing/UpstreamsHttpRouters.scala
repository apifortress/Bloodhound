package com.apifortress.afthem.routing

import com.apifortress.afthem.config.Backend

import scala.collection.mutable

object UpstreamsHttpRouters {

  val routers : mutable.Map[Int, UpstreamsHttpRouter] = mutable.Map.empty[Int, UpstreamsHttpRouter]

  def getRouter(backend : Backend) : UpstreamsHttpRouter = {
    val routerOption = routers.get(backend.hashCode())
    if(routerOption.isDefined)
      return routerOption.get
    val router = new UpstreamsHttpRouter(backend)
    routers.put(backend.hashCode(),router)
    return router
  }

  def getUrl(backend : Backend) : String = {
    if(backend.upstream != null)
      return backend.upstream
    if(backend.upstreams == null || backend.upstreams.urls.size == 0)
      return null
    return getRouter(backend).getNextUrl()
  }

}

class UpstreamsHttpRouter(val backend : Backend) {

  var index : Int = 0

  def getNextUrl() : String = {
    index = ((index+1) % backend.upstreams.urls.size)
    return backend.upstreams.urls(index)
  }

}