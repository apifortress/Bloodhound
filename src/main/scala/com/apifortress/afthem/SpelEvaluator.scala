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

package com.apifortress.afthem

import com.apifortress.afthem.config.AfthemCache
import org.springframework.expression.Expression
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

/**
  * Object caring about everything concerning SpEL expressions
  */
object SpelEvaluator {

  /**
    * The SpEL parser
    */
  val parser = new SpelExpressionParser

  /**
    * If the expression has already been parsed and cached, the cache is returned.
    * Otherwise, the expression gets parsed, cached and returned
    * @param expression the SPeL expression to be parsed
    * @return a parsed expression
    */
  def parse(expression : String) : Expression = {
    return this.synchronized {
      var i2 = AfthemCache.expressionsCache.get(expression)
      if (i2 != null) i2
      else {
        i2 = parser.parseExpression(expression)
        AfthemCache.expressionsCache.put(expression, i2)
        i2
      }
    }
  }

  def evaluate(expression : String, variables : Map[String,Any]) : Any = {
    val parsedException = parse(expression)
    val ctx = new StandardEvaluationContext()
    variables.foreach(item => ctx.setVariable(item._1,item._2))
    return parsedException.getValue(ctx)
  }
}
