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

import java.util.concurrent.TimeUnit
import com.apifortress.afthem.actors.AppContext
import com.apifortress.afthem.config.ApplicationConf
import org.apache.http.HttpResponse
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager
import org.apache.http.impl.nio.reactor.{DefaultConnectingIOReactor, IOReactorConfig}

/**
  * A shared asynchronous HTTP client for all Afthem needs
  */
object AfthemHttpClient {

  /**
    * The object storing the application.properties
    */
  private val applicationConf =  if(AppContext.springApplicationContext != null)
                                    AppContext.springApplicationContext.getBean(classOf[ApplicationConf])
                                  else
                                    null
  /**
    * Max number of I/O threads HTTP Client is allowed to spawn
    */
  private val maxThreads : Int = if(applicationConf!=null)
                                    applicationConf.httpClientMaxThreads
                                  else
                                      1
  /**
    * Max number of simultaneous connections the connection manager is allowed to handle
    */
  private val maxConnections : Int = if(applicationConf!=null)
                                          applicationConf.httpClientMaxConnections
                                        else
                                          100
  /**
    * After how long an idle connection should be marked for eviction
    */
  private val idleTimeoutSeconds : Int = if(applicationConf!=null)
                                          applicationConf.httpClientIdleTimeoutSeconds
                                        else
                                           5

  private val reactorConfig = IOReactorConfig.custom().setIoThreadCount(maxThreads).build()
  private val ioReactor = new DefaultConnectingIOReactor(reactorConfig)
  private val connectionManager = new PoolingNHttpClientConnectionManager(ioReactor)

  connectionManager.setMaxTotal(maxConnections)

  /**
    * The default request configuration
    */
  private val requestConfig = RequestConfig.custom().setConnectTimeout(5000)
                                        .setSocketTimeout(10000)
                                        .setRedirectsEnabled(true)
                                        .setMaxRedirects(5).build()

  /**
    * Finally, the HTTP Client
    */
  val httpClient : CloseableHttpAsyncClient = HttpAsyncClients.custom().disableCookieManagement()
                                                .setConnectionManager(connectionManager)
                                                .setDefaultRequestConfig(requestConfig).build()

  httpClient.start()

  /**
    * Executes an asynchronous HTTP call
    * @param request the request
    * @param callback the callback to be called when the response is ready
    */
  def execute(request : HttpUriRequest, callback : FutureCallback[HttpResponse]): Unit ={
    httpClient.execute(request,callback)
  }

  /**
    * Closes expired connection and any idle connection that has been idle for `idleTimeoutSeconds`
    */
  def closeStaleConnections() : Unit = {
    connectionManager.closeExpiredConnections()
    connectionManager.closeIdleConnections(idleTimeoutSeconds, TimeUnit.SECONDS)
  }
}