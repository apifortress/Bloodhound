package com.apifortress.afthem.actors.sidecars

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.{WebParsedRequestMessage, WebParsedResponseMessage}


/**
  * The actor in charge of audit-logging requests from clients and to origins
  */
class AccessLoggerActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  override def receive: Receive = {
    case msg: WebParsedRequestMessage => println(msg.date+" - "+msg.request.remoteIP+" - "+msg.request.url)
    case msg: WebParsedResponseMessage => println(msg.date+" - "+msg.response.url)
  }

}
