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

import com.apifortress.afthem.{Metric, Parsers, SpelEvaluator}
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.BaseMessage
import org.springframework.expression.spel.support.StandardEvaluationContext

class DeserializerActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  override def receive: Receive = {
    case msg : BaseMessage =>
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
  }

  def deserialize(data : Any, contentType : String): Any = {
    var output : Any = null
    if(data.isInstanceOf[String]) {
      if(contentType.contains("json"))
        output = Parsers.parseJSON[Any](data.asInstanceOf[String], classOf[Any])
      if(contentType.contains("xml"))
        output = Parsers.parseXML[Any](data.asInstanceOf[String], classOf[Any])
    }
    if(data.isInstanceOf[Array[Byte]]) {
      if(contentType.contains("json"))
        output = Parsers.parseJSON[Any](data.asInstanceOf[Array[Byte]], classOf[Any])
      if(contentType.contains("xml"))
        output = Parsers.parseXML[Any](data.asInstanceOf[Array[Byte]], classOf[Any])
    }
    return output
  }
}

