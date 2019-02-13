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
package com.apifortress.afthem.actors.transformers

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage, WebParsedResponseMessage}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.expression.Expression
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

object DeserializerActor {

  val objectMapper = new ObjectMapper()
  objectMapper.registerModule(DefaultScalaModule)

  val xmlMapper = new XmlMapper()
  xmlMapper.registerModule(DefaultScalaModule)

  val parser = new SpelExpressionParser
}
class DeserializerActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  val parsedExpression : Expression = DeserializerActor.parser.parseExpression(phase.config.get("expression").get.asInstanceOf[String])
  val meta : String = phase.config.get("meta").getOrElse(None).asInstanceOf[String]
  val contentType : String = phase.config.get("contentType").getOrElse(None).asInstanceOf[String]

  override def receive: Receive = {
    case msg : BaseMessage => {

      val ctx = new StandardEvaluationContext()
      ctx.setVariable("msg",msg)
      val output = deserialize(parsedExpression.getValue(ctx))

      msg.meta.put(meta,output)

      forward(msg)
    }
  }

  def deserialize(data : Any ): Any = {
    var output : Any = null
    if(data.isInstanceOf[String]) {
      if(contentType.contains("json"))
        output = DeserializerActor.objectMapper.readValue(data.asInstanceOf[String], classOf[Any])
      if(contentType.contains("xml"))
        output = DeserializerActor.xmlMapper.readValue(data.asInstanceOf[String], classOf[Any])
    }
    if(data.isInstanceOf[Array[Byte]]) {
      if(contentType.contains("json"))
        output = DeserializerActor.objectMapper.readValue(data.asInstanceOf[Array[Byte]], classOf[Any])
      if(contentType.contains("xml"))
        output = DeserializerActor.xmlMapper.readValue(data.asInstanceOf[Array[Byte]], classOf[Any])
    }
    return output
  }
}

