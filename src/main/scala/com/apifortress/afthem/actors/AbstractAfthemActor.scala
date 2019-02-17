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

abstract class AbstractAfthemActor(phaseId: String) extends Actor {

  val log = LoggerFactory.getLogger(this.getClass)

  log.info("Initializing "+self.path.toStringWithoutAddress+" - "+context.dispatcher)

  val metricsLog = LoggerFactory.getLogger("_metrics."+getClass.getSimpleName)

  def getPhaseId() : String = return phaseId

  def getPhase(message : BaseMessage) : Phase = message.flow.getPhase(getPhaseId())

  def selectNextActor(message : BaseMessage): ActorSelection = {
    return AppContext.actorSystem.actorSelection("/user/"+message.flow.getNextPhase(getPhaseId()).id)
  }

  private def selectSidecarsActors(message : BaseMessage): List[ActorSelection] = {
    val sidecarIds = message.flow.getPhase(getPhaseId()).sidecars
    if (sidecarIds == null)
      return List.empty[ActorSelection]
    return sidecarIds.map(id => AppContext.actorSystem.actorSelection("/user/"+id))
  }

  def tellSidecars(message : BaseMessage) = {
    selectSidecarsActors(message).foreach( actor => actor ! message)
  }

  def forward(message: BaseMessage) : Unit = {
    tellSidecars(message)
    val selector = selectNextActor(message)
    selector ! message
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info(self.path+" stopped")
  }


}
