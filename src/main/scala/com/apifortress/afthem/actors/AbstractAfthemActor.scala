package com.apifortress.afthem.actors

import com.apifortress.afthem.config.Phases
import com.apifortress.afthem.config.Phase
import akka.actor.{Actor, ActorSelection}
import com.apifortress.afthem.messages.BaseMessage
import org.slf4j.LoggerFactory


abstract class AbstractAfthemActor(phaseId: String) extends Actor {

  val log = LoggerFactory.getLogger(this.getClass)

  log.info("Initializing "+self.path.toStringWithoutAddress)

  val phases = Phases.load()

  val phase = getPhase()

  def getPhaseId() : String = return phaseId

  def getPhase() : Phase = phases.getPhase(getPhaseId())

  def getNextPhase() : Phase = phases.getPhase(getPhase().next)

  def selectActor(phase: Phase) = AppContext.getActorByPhase(phase)

  def selectNextActor() = selectActor(getNextPhase())

  def selectSidecarActors() : List[ActorSelection] = {
    val sidecars = getPhase().sidecars
    if(sidecars == null)
      return List.empty[ActorSelection]
    sidecars.map(phaseId => AppContext.getActorByPhaseId(phaseId))
  }

  def tellSidecars(message: BaseMessage) : Unit = selectSidecarActors().foreach(actor => actor ! message)

  def forward(message: BaseMessage) : Unit = {
    tellSidecars(message)
    selectNextActor() ! message

  }


}
