package com.apifortress.afthem.actors

import com.apifortress.afthem.config.Phases
import com.apifortress.afthem.config.Phase
import akka.actor.{Actor, ActorSelection}
import com.apifortress.afthem.messages.BaseMessage
import org.slf4j.LoggerFactory

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
