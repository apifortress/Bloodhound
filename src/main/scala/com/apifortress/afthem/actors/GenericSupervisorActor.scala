package com.apifortress.afthem.actors
import akka.actor.{Props, Actor}
import akka.routing.FromConfig
import com.apifortress.afthem.messages.StartActorsCommand

class GenericSupervisorActor(val id : String) extends Actor {

  override def receive: Receive = {
    case cmd : StartActorsCommand =>
      cmd.implementers.foreach{ implementer =>
        val theClass = Class.forName(implementer.className)
        context.actorOf(FromConfig.getInstance.props(Props.create(theClass,id+"/"+implementer.id)), implementer.id)
      }
  }
}