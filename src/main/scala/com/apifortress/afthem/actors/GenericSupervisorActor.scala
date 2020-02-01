 /*
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
package com.apifortress.afthem.actors

import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.actor.{Actor, OneForOneStrategy, Props, SupervisorStrategy}
import akka.routing.SmallestMailboxPool
import com.apifortress.afthem.ReqResUtil
import com.apifortress.afthem.exceptions.{AfthemFlowException, AfthemSevereException}
import com.apifortress.afthem.messages.{ExceptionMessage, StartActorsCommand}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration._

/**
  * A supervisor and creator of all other actors
  * @param id the ID of the supervisor
  */
class GenericSupervisorActor(val id : String) extends Actor {

  /**
    * The logger
    */
  val log : Logger = LoggerFactory.getLogger(classOf[GenericSupervisorActor])

  log.info("Initializing supervisor "+self.path.toStringWithoutAddress+" - "+context.dispatcher)

  /**
    * The supervisor. Known caught exceptions (AFthemFlowException) cause a "Resume", unknown cause a Restart
    */
  override val supervisorStrategy : SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case exception : AfthemFlowException =>
        new ExceptionMessage(exception,500,exception.message).respond(ReqResUtil.extractAcceptFromMessage(exception.message))
        Resume
      case exception : AfthemSevereException =>
        new ExceptionMessage(exception,500,exception.message).respond(ReqResUtil.extractAcceptFromMessage(exception.message))
        Restart
      case _ => Restart
    }
  override def receive: Receive = {
    case cmd : StartActorsCommand =>
      cmd.implementers.foreach{ implementer =>
        val tp = if (implementer.threadPool != null) implementer.threadPool else AppContext.DISPATCHER_DEFAULT
        val theClass = Class.forName(implementer.className)
        var ref = Props.create(theClass,id+"/"+implementer.id).withDispatcher(tp)
        if(implementer.instances > 1)
          ref = SmallestMailboxPool(implementer.instances).props(routeeProps = ref).withDispatcher(tp)
        context.actorOf(ref, implementer.id)
      }
  }
}