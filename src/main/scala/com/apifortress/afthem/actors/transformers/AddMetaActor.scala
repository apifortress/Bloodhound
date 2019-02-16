/*
 *   Copyright 2019 API Fortress
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   @author Simone Pezzano
 *
 */

package com.apifortress.afthem.actors.transformers

import com.apifortress.afthem.{Metric, SpelEvaluator}
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.BaseMessage
import org.springframework.expression.spel.support.StandardEvaluationContext

class AddMetaActor(id: String) extends AbstractAfthemActor(id: String) {

  override def receive: Receive = {
    case msg : BaseMessage =>
      val m = new Metric
      val phase = getPhase(msg)
      val evaluated = phase.getConfigBoolean("evaluated").getOrElse(false)
      val data = phase.getConfigString("data")
      val name = phase.getConfigString("name")
      if(evaluated) {
        val parsedExpression = SpelEvaluator.parse(data)
        val ctx = new StandardEvaluationContext()
        ctx.setVariable("msg",msg)
        msg.meta.put(name,parsedExpression.getValue(ctx))
      } else msg.meta.put(name,data)
      forward(msg)
      log.debug(m.toString())
  }
}
