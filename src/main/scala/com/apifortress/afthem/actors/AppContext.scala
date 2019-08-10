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

import java.io.File

import akka.actor.{ActorSystem, Props}
import akka.routing.FromConfig
import com.apifortress.afthem.config.Implementers
import com.apifortress.afthem.messages.StartActorsCommand
import com.typesafe.config.ConfigFactory

/**
  * Application context. Initializes actors on load and provides useful methods to play with them
  */
object AppContext {

  /**
    * The Akka configuration
    */
  private val config = ConfigFactory.parseFile(new File("etc"+File.separator+"application.conf"))
  /**
    * The actor system
    */
  val actorSystem : ActorSystem = ActorSystem.create("afthem",config)

   /*
    * For each implementer, we create an actor.
    */
  val types : List[String] = Implementers.instance.implementers.map(item=> item.actorType).distinct
  types.foreach { actorType =>
    val supervisor = actorSystem.actorOf(Props.create(classOf[GenericSupervisorActor],actorType),actorType)
    supervisor ! StartActorsCommand(Implementers.instance.implementers.filter(item => item.actorType == actorType),config)
  }

  def init() : Unit = {}


}
