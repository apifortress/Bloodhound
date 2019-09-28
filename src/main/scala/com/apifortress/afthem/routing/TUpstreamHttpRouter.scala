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

  /**
    * A probe configuration
    */
  var probe : Probe = null

  /**
    * A scheduled probe task
    */
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

    urls = backend.upstreams.urls.map(it => new RoutedUrl(it, backend.upstreams.probe))

    updateProbe(backend.upstreams.probe)


  }

  /**
    * Updates the probe configuration if necessary. If the probe configuration required an update
    * follow up actions to the schedule are applied
    * @param probe a new probe configuration
    */
  private def updateProbe(probe : Probe) : Unit = {
    val oldProbe = this.probe
    this.probe = probe
    // An existing probe exists
    if(oldProbe != null && probe != null) {
      // And the new probe and the old probe are not equivalent
      if(oldProbe.hashCode() != probe.hashCode()) {
        TUpstreamHttpRouter.log.debug("Cancelling previous probe due to update")
        restartProbe()
      }
    }
    // An existing probe exists, and the new configuration has no probe
    else if(oldProbe != null && probe == null) {
      TUpstreamHttpRouter.log.debug("Cancelling previous probe as probe got removed from configuration")
      cancelProbe()
    // There's no current probe and the new configuration has a probe
    } else if(oldProbe == null && probe != null){
      TUpstreamHttpRouter.log.debug("Starting a probe that wasn't running before")
      startProbe()
    }
  }

  /**
    * If a probe is running, cancel it
    */
  def cancelProbe() : Unit = {
    if(cancellableProbeTask != null) {
      TUpstreamHttpRouter.log.debug("Cancelling probe")
      cancellableProbeTask.cancel()
    }
  }

  /**
    * Starts a probe, if a probe configuration is present
    */
  def startProbe() : Unit = {
    if(probe != null) {
      TUpstreamHttpRouter.log.debug("Starting probe")
      val duration = Duration(probe.interval).asInstanceOf[FiniteDuration]
      implicit val executor = if(AppContext.actorSystem.dispatchers.hasDispatcher(AppContext.DISPATCHER_PROBE))
                                      AppContext.actorSystem.dispatchers.lookup(AppContext.DISPATCHER_PROBE)
                              else
                                  AppContext.actorSystem.dispatchers.lookup(AppContext.DISPATCHER_DEFAULT)

      implicit val sender : ActorRef = null
      cancellableProbeTask = AppContext.actorSystem.scheduler.schedule(duration, duration,
                                                                        AppContext.probeHttpActor, this)
    }
  }

  /**
    * Restart a probe
    */
  def restartProbe() : Unit = {
    cancelProbe()
    startProbe()
  }

}
