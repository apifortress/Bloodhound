package com.apifortress.afthem.actors.probing

import akka.actor.Actor
import com.apifortress.afthem.AfthemHttpClient
import com.apifortress.afthem.routing.{RoutedUrl, TUpstreamHttpRouter}
import org.apache.http.HttpResponse
import org.apache.http.concurrent.FutureCallback
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory

class ProbeHttpActor extends Actor {

  private val log = LoggerFactory.getLogger(this.getClass)

  override def receive: Receive = {
    case msg : TUpstreamHttpRouter => {
      log.debug("Received router for probes")
      msg.urls.foreach(it => self ! it)
    }
    case msg : RoutedUrl => {
      log.debug("Probing "+msg.url)
      val baseRequest = AfthemHttpClient.createBaseRequest(msg.probe.method,msg.url+msg.probe.path)
      AfthemHttpClient.execute(baseRequest, new FutureCallback[HttpResponse] {
        override def completed(t: HttpResponse): Unit = {
          if(t.getStatusLine.getStatusCode == msg.probe.status) {
            log.debug("Status code was the one expected, voting up for "+msg.url)
            msg.addStatus(true)
          }
          else {
            log.debug("Status code was not the one expected, voting down for "+msg.url)
            msg.addStatus(false)
          }
          val entity = t.getEntity
          if(entity != null)
            EntityUtils.consume(entity)
        }

        override def failed(e: Exception): Unit = {
          log.debug("Request failed for "+msg.url)
          msg.addStatus(false)
        }

        override def cancelled(): Unit = {}
      })
    }
  }
}
