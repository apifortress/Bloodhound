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
import com.apifortress.afthem.messages.BaseMessage
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.Expression
import org.springframework.expression.spel.support.StandardEvaluationContext

object GenericLoggerActor {

  val parser = new SpelExpressionParser

}

class GenericLoggerActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  override def receive: Receive = {
    case msg : BaseMessage =>
      val parsedExpression = GenericLoggerActor.parser.parseExpression(getPhase(msg).config.get("expression").get.asInstanceOf[String])
      val ctx = new StandardEvaluationContext()
      ctx.setVariable("msg",msg)
      println(parsedExpression.getValue(ctx))

  }

}
