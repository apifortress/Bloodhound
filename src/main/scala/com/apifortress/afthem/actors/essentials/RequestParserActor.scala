package com.apifortress.afthem.actors.essentials

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.{HttpWrapper, WebParsedRequestMessage, WebRawRequestMessage}
import com.apifortress.afthem.{ReqResUtil, UriUtil}
import org.slf4j.LoggerFactory

/**
  * The actor in charge of parsing the inbound request
  */

object RequestParserActor {

  val log = LoggerFactory.getLogger("RequestParserActor")

}
class RequestParserActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  override def receive: Receive = {
    case msg : WebRawRequestMessage => {
      val wrapper = new HttpWrapper

      wrapper.url = UriUtil.composeUriAndQuery(msg.request.getRequestURL.toString,msg.request.getQueryString)

      val parsedHeaders = ReqResUtil.parseHeaders(msg.request)

      wrapper.headers = parsedHeaders._1

      wrapper.payload = ReqResUtil.readPayload(msg.request.getInputStream,parsedHeaders._2.get("content-length"))

      wrapper.method = msg.request.getMethod.toUpperCase

      wrapper.remoteIP = msg.request.getRemoteAddr

      val message = new WebParsedRequestMessage(wrapper,msg.deferredResult,msg.date,msg.meta)

      forward(message)
    }
  }

}
