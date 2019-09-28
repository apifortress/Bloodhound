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

import java.util.Objects

import com.apifortress.afthem.config.Probe
import org.slf4j.LoggerFactory
import scala.util.control.Breaks._
import scala.collection.mutable

/**
  * Companion object for the RoutedUrl class
  */
object RoutedUrl {

  /**
    * The logger
    */
  val log = LoggerFactory.getLogger(classOf[RoutedUrl])

}

/**
  * Represents a URL with its probe configuration
  * @param url a URL
  * @param probe a probe configuration
  */
class RoutedUrl(val url: String, val probe : Probe) {

  /**
    * A queue of the states of the probe activities. Head is the latest
    */
  private var probingQueue : mutable.ListBuffer[Boolean] = new mutable.ListBuffer[Boolean]()

  /**
    * The status of the URL. True means the URL is operative
    */
  var upStatus : Boolean = true


  /**
    * Adds the result of a probe execution and evaluates whether this routedUrl should be considered
    * up or down, based on the probe configuration
    * @param up true if the result of the probe execution was successful
    */
  def addStatus(up: Boolean) : Unit = {
    probingQueue.insert(0,up)

    // The maximum number of meaning probe results among count up and count down
    val limit = Math.max(probe.countUp,probe.countDown)

    // There's no reason for the probing queue to be longer than the limit, so we slice it accordingly
    if(probingQueue.size > limit)
      probingQueue = probingQueue.slice(0,limit)

    // If the URL is currently considered UP...
    if(upStatus){
      var countFalse = 0
      breakable {
        // We count how many consecutive failures we have in the queue starting with the latest item
        for (i <- 0 until Math.min(probe.countDown, probingQueue.size))
          if (probingQueue(i) == false)
            countFalse += 1
          else
            break
      }
      // If the count equals to the count down probe configuration, it means we should take the URL down
      if(countFalse == probe.countDown) {
        RoutedUrl.log.info(url+" is failing. Stepping down")
        upStatus = false
      }
    }
     // If the URL is currently considered down
     else {
      var countTrue = 0
      breakable {
        // We count how many consecutive successes we have in the queue starting with the latest item
        for (i <- 0 until Math.min(probe.countUp, probingQueue.size))
          if (probingQueue(i) == true)
            countTrue += 1
          else
            break
      }
      // If the count equals to the count up probe configuration, it means we should bring the URL up
      if(countTrue == probe.countUp) {
        RoutedUrl.log.info(url+" is back operational. Stepping up")
        upStatus = true
      }
    }
  }

  override def hashCode(): Int = {
    return Objects.hash(url,probe)
  }

}