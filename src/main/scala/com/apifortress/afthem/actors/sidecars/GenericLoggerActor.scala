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

  val parsedExpression : Expression = GenericLoggerActor.parser.parseExpression(phase.config.get("expression").get.asInstanceOf[String])

  override def receive: Receive = {
    case msg : BaseMessage =>
      val ctx = new StandardEvaluationContext()
      ctx.setVariable("msg",msg)
      println(parsedExpression.getValue(ctx))

  }

}
