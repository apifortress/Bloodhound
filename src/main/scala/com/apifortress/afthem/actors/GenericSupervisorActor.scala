package com.apifortress.afthem.actors

import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.actor.{Actor, OneForOneStrategy, Props, SupervisorStrategy}
import akka.routing.SmallestMailboxPool
import com.apifortress.afthem.ReqResUtil
import com.apifortress.afthem.exceptions.AfthemFlowException
import com.apifortress.afthem.messages.{ExceptionMessage, StartActorsCommand}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration._

/**
  * A supervisor and creator of all other actors
  * @param id the ID of the supervisor
  */
class GenericSupervisorActor(val id : String) extends Actor {

  val log : Logger = LoggerFactory.getLogger(classOf[GenericSupervisorActor])

  log.info("Initializing supervisor "+self.path.toStringWithoutAddress+" - "+context.dispatcher)

  override val supervisorStrategy : SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case exception : AfthemFlowException =>
        new ExceptionMessage(exception,500,exception.message).respond(ReqResUtil.extractAcceptFromMessage(exception.message))
        Resume
      case _ => Restart
    }
  override def receive: Receive = {
    case cmd : StartActorsCommand =>
      cmd.implementers.foreach{ implementer =>
        val tp = if (implementer.threadPool != null) implementer.threadPool else "default"
        val theClass = Class.forName(implementer.className)
        var ref = Props.create(theClass,id+"/"+implementer.id).withDispatcher(tp)
        if(implementer.instances > 1)
          ref = SmallestMailboxPool(implementer.instances).props(routeeProps = ref).withDispatcher(tp)
        context.actorOf(ref, implementer.id)
      }
  }
}