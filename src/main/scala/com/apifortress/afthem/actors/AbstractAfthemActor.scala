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
package com.apifortress.afthem.actors
import akka.actor.{Actor, ActorSelection}
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.messages.BaseMessage
import org.slf4j.LoggerFactory
import org.slf4j.Logger

abstract class AbstractAfthemActor(phaseId: String) extends Actor {

  protected val log : Logger = LoggerFactory.getLogger(this.getClass)

  log.info("Initializing "+self.path.toStringWithoutAddress+" - "+context.dispatcher)

  protected val metricsLog : Logger = LoggerFactory.getLogger("_metrics."+getClass.getSimpleName)

  def getPhaseId() : String = return phaseId

  def getPhase(message : BaseMessage) : Phase = message.flow.getPhase(getPhaseId())

  def selectNextActor(message : BaseMessage): ActorSelection = {
    val nextId = message.flow.getNextPhase(getPhaseId()).id
    if(nextId.startsWith("akka://"))
      return AppContext.actorSystem.actorSelection(nextId)
    else
      return AppContext.actorSystem.actorSelection("/user/" + nextId)
  }

  private def selectSidecarsActors(message : BaseMessage): List[ActorSelection] = {
    val sidecarIds = getPhase(message).sidecars
    if (sidecarIds == null)
      return List.empty[ActorSelection]
    return sidecarIds.map(id => AppContext.actorSystem.actorSelection("/user/"+id))
  }

  protected def tellSidecars(message : BaseMessage) : Unit = {
    selectSidecarsActors(message).foreach( actor => actor ! message)
  }

  protected def hasSidecarActors(message : BaseMessage) : Boolean = {
    return getPhase(message).sidecars != null
  }

  protected def forward(message: BaseMessage) : Unit = {
    if(hasSidecarActors(message))
      tellSidecars(message.clone())
    tellNextActor(message)
  }

  protected def tellNextActor(message: BaseMessage) : Unit = {
    val selector = selectNextActor(message)
    selector ! message
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info(self.path+" stopped")
  }


}
