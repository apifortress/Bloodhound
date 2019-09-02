package com.apifortress.afthem

import com.apifortress.afthem.actors.AppContext
import com.apifortress.afthem.config.ApplicationConf
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager
import org.apache.http.impl.nio.reactor.{DefaultConnectingIOReactor, IOReactorConfig}

object AfthemHttpClient {

  val reactorConfig = IOReactorConfig.custom()
    .setIoThreadCount(AppContext.springApplicationContext.getBean(classOf[ApplicationConf]).httpClientMaxThreads).build()
  val ioReactor = new DefaultConnectingIOReactor(reactorConfig)
  val connectionManager = new PoolingNHttpClientConnectionManager(ioReactor)
  val httpClient : CloseableHttpAsyncClient = HttpAsyncClients.custom().disableCookieManagement().setConnectionManager(connectionManager).build()
  httpClient.start()

  def execute(request : HttpUriRequest, callback : FutureCallback[HttpResponse]): Unit ={
    httpClient.execute(request,callback)
  }
}