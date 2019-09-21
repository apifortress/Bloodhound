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


object AbstractAfthemActor {
  /**
    * Determines what the next actor in the chain is
    * @param message a message
    * @return an actorSelection representing the next actor in the chain
    */
  def selectNextActor(message : BaseMessage, phaseId : String): ActorSelection = {
    val nextId = message.flow.getNextPhase(phaseId).id
    if(nextId.startsWith("akka://"))
      return AppContext.actorSystem.actorSelection(nextId)
    else
      return AppContext.actorSystem.actorSelection("/user/" + nextId)
  }
}
/**
  * The base of all actors used in Afthem
  * @param phaseId the phaseId
  */
abstract class AbstractAfthemActor(val phaseId: String) extends Actor {

  protected val log : Logger = LoggerFactory.getLogger(this.getClass)

  log.info("Initializing "+self.path.toStringWithoutAddress+" - "+context.dispatcher)

  protected val metricsLog : Logger = LoggerFactory.getLogger("_metrics."+getClass.getSimpleName)

  /**
    * @return the phase ID
    */
  def getPhaseId() : String = return phaseId

  /**
    * Given a message, retrieve the phase using the message ID received in the constructor.
    * @param message the message
    * @return the phase
    */
  def getPhase(message : BaseMessage) : Phase = message.flow.getPhase(getPhaseId())


  /**
    * Determines which sidecars need to be informed of the message
    * @param message the message
    * @return a list of ActorSelection objects representing the sidecars actors
    */
  private def selectSidecarsActors(message : BaseMessage): List[ActorSelection] = {
    val sidecarIds = getPhase(message).sidecars
    if (sidecarIds == null)
      return List.empty[ActorSelection]
    return sidecarIds.map(id => AppContext.actorSystem.actorSelection("/user/"+id))
  }

  /**
    * Forwards the message to sidecars
    * @param message the message to be forwarded
    */
  protected def tellSidecars(message : BaseMessage) : Unit = {
    selectSidecarsActors(message).foreach( actor => actor ! message)
  }

  /**
    * Finds out if the current actor has sidecars
    * @param message the message
    * @return true if the current actor has sidecars
    */
  protected def hasSidecarActors(message : BaseMessage) : Boolean = {
    return getPhase(message).sidecars != null
  }

  /**
    * Moves the message forward by sending the message both to the sidecars and the next item in the chain
    * @param message the message
    */
  protected def forward(message: BaseMessage) : Unit = {
    if(hasSidecarActors(message))
      tellSidecars(message.shallowClone(true))
    tellNextActor(message)
  }

  /**
    * Forwards the message to the next actor in the chain
    * @param message the message
    */
  protected def tellNextActor(message: BaseMessage) : Unit = {
    val selector = AbstractAfthemActor.selectNextActor(message,getPhaseId())
    selector ! message
  }

  def getLog : Logger = return log

  override def postStop(): Unit = {
    super.postStop()
    log.info(self.path+" stopped")
  }


}
