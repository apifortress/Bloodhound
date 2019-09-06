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

import com.apifortress.afthem.exceptions.AfthemFlowException
import com.apifortress.afthem.{Metric, Parsers, SpelEvaluator}
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.BaseMessage
import org.springframework.expression.spel.support.StandardEvaluationContext

/**
  * Actor that deserializes XML and JSON and puts the result into the metas of the message
  * @param phaseId the phaseId the ID of the phase
  */
class DeserializerActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  override def receive: Receive = {
    case msg : BaseMessage =>
      try{
        val m = new Metric
        val phase = getPhase(msg)

        val parsedExpression = SpelEvaluator.parse(phase.getConfigString("expression"))

        val meta = phase.getConfigString("meta")
        val contentType  = phase.getConfigString("contentType")

        val ctx = new StandardEvaluationContext()
        ctx.setVariable("msg",msg)
        val output = deserialize(parsedExpression.getValue(ctx),contentType)

        msg.meta.put(meta,output)

        forward(msg)
        metricsLog.debug(m.toString())
      }catch {
        case e : Exception =>
          log.error("Exception during the deserializer operation",e)
          throw new AfthemFlowException(msg,e.getMessage)
      }
  }

  /**
    * Deserializes data
    * @param data the data to be deserialized
    * @param contentType the expected content type
    * @return the deserialized content
    */
  def deserialize(data : Any, contentType : String): Any = {
    var output : Any = null
    data match {
      case data : String =>
        if(contentType.contains("json"))
          output = Parsers.parseJSON[Any](data.asInstanceOf[String], classOf[Any])
        if(contentType.contains("xml"))
          output = Parsers.parseXML[Any](data.asInstanceOf[String], classOf[Any])
      case data : Array[Byte] =>
        if(contentType.contains("json"))
          output = Parsers.parseJSON[Any](data.asInstanceOf[Array[Byte]], classOf[Any])
        if(contentType.contains("xml"))
          output = Parsers.parseXML[Any](data.asInstanceOf[Array[Byte]], classOf[Any])
    }
    return output
  }
}

