package com.apifortress.afthem.actors.transformers

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.{WebParsedRequestMessage, WebParsedResponseMessage}
import org.apache.commons.codec.binary.StringUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object BeautifyPayloadActor {

  val objectMapper: ObjectMapper = new ObjectMapper
  objectMapper.registerModule(DefaultScalaModule)

  val xmlMapper : XmlMapper = new XmlMapper
  xmlMapper.registerModule(DefaultScalaModule)

}

class BeautifyPayloadActor(phaseId : String) extends AbstractAfthemActor(phaseId : String) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage => {
      msg.request.payload = beautify(msg.request.payload)
      msg.request.headers = msg.request.headers.filter( header => header._1.toLowerCase!="content-length")
      forward(msg)
    }
    case msg : WebParsedResponseMessage => {
      msg.response.payload = beautify(msg.response.payload)
      msg.response.headers = msg.response.headers.filter( header => header._1.toLowerCase!="content-length")
      forward(msg)
    }
  }

  def beautify(data : Array[Byte]) : Array[Byte] = {
    val mode = phase.config.get("mode").getOrElse("json").asInstanceOf[String]
    if (mode.contains("json")) {
      val obj = BeautifyPayloadActor.objectMapper.readValue(data, classOf[Object])
      return StringUtils.getBytesUtf8(BeautifyPayloadActor.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj))
    }
    if (mode.contains("xml")) {
      val obj = BeautifyPayloadActor.objectMapper.readValue(data, classOf[Object])
      return StringUtils.getBytesUtf8(BeautifyPayloadActor.xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj))
    }
    return data
  }
}
