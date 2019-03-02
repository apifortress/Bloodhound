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

import com.apifortress.afthem.SpelEvaluator
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.BaseMessage
import org.slf4j.LoggerFactory
import org.springframework.expression.spel.support.StandardEvaluationContext

/**
  * A generic logger actor that will log a piece of information you will provide as configuration
  *
  * @param phaseId the phase ID
  */
class GenericLoggerActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  val genericLoggerLog = LoggerFactory.getLogger("Generic")

  override def receive: Receive = {
    case msg : BaseMessage =>
      val config = getPhase(msg).getConfigAsEvalNameValue()
      if(config.evaluated) {
        val parsedExpression = SpelEvaluator.parse(config.value)
        val ctx = new StandardEvaluationContext()
        ctx.setVariable("msg",msg)
        genericLoggerLog.info(parsedExpression.getValue(ctx).toString)
      } else genericLoggerLog.info(config.value)
  }

}
