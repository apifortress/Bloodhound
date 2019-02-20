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
import com.typesafe.config.ConfigFactory

/**
  * Application context. Initializes actors on load and provides useful methods to play with them
  */
object AppContext {



  private val config = ConfigFactory.parseFile(new File("etc"+File.separator+"application.conf"))
  val actorSystem : ActorSystem = ActorSystem.create("afthem",config)

  Implementers.instance.implementers.foreach{ item =>
    val theClass = Class.forName(item.className)
    if(config.hasPath("akka.actor.deployment./"+item.id))
      actorSystem.actorOf(FromConfig.getInstance.props(Props.create(theClass,item.id)), item.id)
    else
      actorSystem.actorOf(Props.create(theClass,item.id), item.id)
  }

  def init() : Unit = {}


}
