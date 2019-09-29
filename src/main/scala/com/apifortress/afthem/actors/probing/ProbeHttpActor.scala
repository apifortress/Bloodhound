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
package com.apifortress.afthem.actors.probing

import akka.actor.Actor
import com.apifortress.afthem.AfthemHttpClient
import com.apifortress.afthem.routing.{RoutedUrl, TUpstreamHttpRouter}
import org.apache.http.HttpResponse
import org.apache.http.client.config.RequestConfig
import org.apache.http.concurrent.FutureCallback
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration


object ProbeHttpActor {
  /**
    * The logger
    */
  val log = LoggerFactory.getLogger(this.getClass)
}
/**
  * The actor executing the probe tasks
  */
class ProbeHttpActor extends Actor {



  override def receive: Receive = {
    case msg : TUpstreamHttpRouter => {
      ProbeHttpActor.log.debug("Received router for probes")
      msg.urls.foreach(it => self ! it)
    }
    case msg : RoutedUrl => {
      ProbeHttpActor.log.debug("Probing "+msg.url)
      val baseRequest = AfthemHttpClient.createBaseRequest(msg.probe.method,msg.url+msg.probe.path)
      val timeoutMillis = Duration(msg.probe.timeout).toMillis.toInt
      val requestConfig = RequestConfig.custom().setConnectTimeout(timeoutMillis)
                                                  .setSocketTimeout(timeoutMillis)
                                                  .setRedirectsEnabled(true)
                                                  .setMaxRedirects(5).build()
      baseRequest.setConfig(requestConfig)
      AfthemHttpClient.execute(baseRequest, new ProbeFutureCallback(msg))
    }
  }
}

/**
  * Probe future
  * @param msg the routedUrl
  */
class ProbeFutureCallback(val msg : RoutedUrl) extends FutureCallback[HttpResponse] {

  override def completed(t: HttpResponse): Unit = {
    // If the status code is the one expected
    if(t.getStatusLine.getStatusCode == msg.probe.status) {
      ProbeHttpActor.log.debug("Status code was the one expected, voting up for " + msg.url)
      msg.addStatus(true)
    }
    // If the status code is not the one expected
    else {
      ProbeHttpActor.log.debug("Status code was not the one expected, voting down for "+msg.url)
      msg.addStatus(false)
    }
    val entity = t.getEntity
    if(entity != null)
      EntityUtils.consume(entity)
  }

  override def failed(e: Exception): Unit = {
    ProbeHttpActor.log.debug("Request failed for "+msg.url)
    msg.addStatus(false)
  }

  override def cancelled(): Unit = {}

}
