package com.apifortress.afthem.routing

import java.util.Objects

import com.apifortress.afthem.config.Probe
import org.slf4j.LoggerFactory

import scala.collection.mutable


object RoutedUrl {

  val log = LoggerFactory.getLogger(classOf[RoutedUrl])

}

class RoutedUrl(val url: String, val probe : Probe) {

  private var probingQueue : mutable.ListBuffer[Boolean] = new mutable.ListBuffer[Boolean]()

  var upStatus : Boolean = true


  def addStatus(up: Boolean) : Unit = {
    probingQueue.insert(0,up)
    val limit = Math.max(probe.countUp,probe.countDown)

    if(probingQueue.size > limit)
      probingQueue = probingQueue.slice(0,limit)
    if(upStatus){
      var countFalse = 0
      for(i <- 0 until Math.min(probe.countDown,probingQueue.size))
        if(probingQueue(i) == false)
          countFalse+=1
      if(countFalse == probe.countDown) {
        RoutedUrl.log.info(url+" is failing. Stepping down")
        upStatus = false
      }
    } else {
      var countTrue = 0
      for(i <- 0 until Math.min(probe.countUp,probingQueue.size))
        if(probingQueue(i) == true)
          countTrue+=1
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