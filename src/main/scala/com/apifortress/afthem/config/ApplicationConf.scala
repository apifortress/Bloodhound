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
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
  * application.properties is generally used for Spring settings and we don't need to access it directly.
  * However, it can also be used for custom properties, therefore we may need to access its content from time to time.
  */
@Component("applicationConf")
class ApplicationConf {

  /**
    * Custom property to force a certain number of I/O threads in the HTTP client
    */
  @Value("${httpclient.max_threads:4}") var httpClientMaxThreads : Integer = null

  /**
    * Maximum number of connections that could be alive at the same time in the connection manager
    */
  @Value("${httpclient.max_connections:100}") var httpClientMaxConnections : Integer = null

  /**
    * How long an idle connection has to be considered to be evicted
    */
  @Value("${httpclient.idle_timeout_seconds:5}") var httpClientIdleTimeoutSeconds : Integer = null

}
