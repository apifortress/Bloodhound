package com.apifortress.afthem.actors.essentials

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.WebParsedResponseMessage
import org.springframework.http.ResponseEntity

class SendBackActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {



  override def receive: Receive = {
    case msg: WebParsedResponseMessage => {
      val response = msg.response
      var envelopeBuilder = ResponseEntity.status(response.status)
      response.headers.foreach( header=> envelopeBuilder=envelopeBuilder.header(header._1,header._2))
      val bodyBuilder = envelopeBuilder.body(response.payload)
      msg.deferredResult.setResult(bodyBuilder)
    }
  }

}
