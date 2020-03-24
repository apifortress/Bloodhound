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
package com.apifortress.afthem.actors.sidecars

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.exceptions.{InvalidContentException, RejectedRequestException, UnauthenticatedException, UnauthorizedException}
import com.apifortress.afthem.messages.{ExceptionMessage, WebParsedRequestMessage, WebParsedResponseMessage}
import org.slf4j.LoggerFactory

/**
  * Actor in charge of logging accesses to the gateway and the upstream API
  * @param phaseId the phase ID
  */
class AccessLoggerActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  /**
    * The inbound logger
    */
  private val accessInboundLog = LoggerFactory.getLogger("Access-Inbound")
  /**
    * The upstream logger
    */
  private val upstreamLog = LoggerFactory.getLogger("Upstream")

  override def receive: Receive = {
    // Logging exceptions
    case msg: ExceptionMessage if(msg.exception.isInstanceOf[RejectedRequestException]) =>
      // If the exception is the result of a request rejection
      val exception = msg.exception.asInstanceOf[RejectedRequestException]
      accessInboundLog.info(exception.message.request.remoteIP + " - " + exception.message.request.method + " " + exception.message.request.getURL() + " [REJECTED]")
    case msg: ExceptionMessage if(msg.exception.isInstanceOf[UnauthorizedException]) =>
      // If the exception is the result of an unauthorized request rejection
      val exception = msg.exception.asInstanceOf[UnauthorizedException]
      accessInboundLog.info(exception.message.request.remoteIP + " - " + exception.message.request.method + " " + exception.message.request.getURL() + " [UNAUTHORIZED]")
    case msg: ExceptionMessage if(msg.exception.isInstanceOf[UnauthenticatedException]) =>
      // If the exception is the result of an unauthenticated request rejection
      val exception = msg.exception.asInstanceOf[UnauthenticatedException]
      accessInboundLog.info(exception.message.request.remoteIP + " - " + exception.message.request.method + " " + exception.message.request.getURL() + " [UNAUTHENTICATED]")
    case msg: ExceptionMessage if(msg.exception.isInstanceOf[InvalidContentException]) =>
      val request = if (msg.message.isInstanceOf[WebParsedRequestMessage])
                       msg.message.asInstanceOf[WebParsedRequestMessage].request
                    else
                      msg.message.asInstanceOf[WebParsedResponseMessage].request
      accessInboundLog.info(request.remoteIP + " - " + request.method + " " + request.getURL() + " [INVALID]")
    case msg: WebParsedRequestMessage =>
      // Logging a request to the gateway
      accessInboundLog.info(msg.request.remoteIP+" - "+msg.request.method+" "+msg.request.getURL())
    case msg: WebParsedResponseMessage =>
      // Logging a response from the upstream SPI
      upstreamLog.info("["+msg.response.status+"] - "+msg.response.method+" "+msg.response.getURL())
  }

}
