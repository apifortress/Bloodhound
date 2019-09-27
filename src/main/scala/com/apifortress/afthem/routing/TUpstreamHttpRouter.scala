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
package com.apifortress.afthem.routing

import akka.actor.{ActorRef, Cancellable}
import com.apifortress.afthem.actors.AppContext
import com.apifortress.afthem.config.{Backend, Probe}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

object TUpstreamHttpRouter {

  val log = LoggerFactory.getLogger(classOf[TUpstreamHttpRouter])
}
/**
  * A trait for all the upstream http routers
  */
trait TUpstreamHttpRouter {

  /**
    * The hash of the backend that generated this router
    */
  var backendHashCode : Int = -1

  /**
    * A list of RoutedUrl. Each represent an upstream URL plus everything that is needed to validate it's vitality
    */
  var urls : List[RoutedUrl] = null

  var probe : Probe = null

  var cancellableProbeTask : Cancellable = null

  /**
    * Computes and returns the next upstream url that needs to be used
    * @return the next upstream url that needs to be used
    */
  def getNextUrl(loop: Int = 0) : String

  /**
    * Updates the router based on the provided backend
    * @param backend a Backend instance
    */
  def update(backend: Backend) : Unit = {
    this.backendHashCode = backend.hashCode

    updateProbe(backend.upstreams.probe)

    urls = backend.upstreams.urls.map(it => new RoutedUrl(it, backend.upstreams.probe))

    startProbe()
  }

  private def updateProbe(probe : Probe) : Unit = {
    if(this.probe != null && probe != null) {
      if(this.probe.hashCode() != probe.hashCode()) {
        TUpstreamHttpRouter.log.debug("Cancelling previous probe due to update")
        cancelProbe()
      }
    }
    else if(this.probe != null && probe == null) {
      TUpstreamHttpRouter.log.debug("Cancelling previous probe as probe got removed from configuration")
      cancelProbe()
    }
    this.probe = probe
  }

  def cancelProbe() : Unit = {
    if(cancellableProbeTask != null) {
      TUpstreamHttpRouter.log.debug("Cancelling probe")
      cancellableProbeTask.cancel()
    }
  }

  def startProbe() : Unit = {
    if(probe != null) {
      TUpstreamHttpRouter.log.debug("Starting probe")
      val duration = Duration(probe.interval).asInstanceOf[FiniteDuration]
      implicit val executor = AppContext.actorSystem.dispatcher
      implicit val sender : ActorRef = null
      cancellableProbeTask = AppContext.actorSystem.scheduler.schedule(duration, duration,
                                                                        AppContext.probeHttpActor, this)
    }
  }

}
