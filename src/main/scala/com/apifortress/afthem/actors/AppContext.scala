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

import akka.actor.{ActorRef, ActorSystem, Props}
import com.apifortress.afthem.AfthemHttpClient
import com.apifortress.afthem.actors.probing.ProbeHttpActor
import com.apifortress.afthem.config.Implementers
import com.apifortress.afthem.messages.StartActorsCommand
import com.typesafe.config.ConfigFactory
import org.springframework.context.ApplicationContext

import scala.concurrent.duration._

/**
  * Application context. Initializes actors on load and provides useful methods to play with them
  */
object AppContext {


  /**
    * Akka textual configuration
    */
  private val cfg : StringBuffer = new StringBuffer()
  Implementers.instance.threadPools.foreach { pool =>
      cfg.append(s"${pool._1} {\n")
      cfg.append("\ttype = \"Dispatcher\"\n")
      cfg.append("\texecutor = \"fork-join-executor\"\n")
      cfg.append("\tfork-join-executor {\n")
      cfg.append(s"\t\tparallelism-min=${pool._2.min}\n")
      cfg.append(s"\t\tparallelism-max=${pool._2.max}\n")
      cfg.append(s"\t\tparallelism-factor=${pool._2.factor}\n")
      cfg.append("\t}\n")
      cfg.append("}\n")
  }
  /**
    *
    * The Akka configuration
    */
  private val config = ConfigFactory.parseString(cfg.toString)
  /**
    * The actor system
    */
  val actorSystem : ActorSystem = ActorSystem.create("afthem",config)
  implicit val executionContext = actorSystem.dispatcher

   /*
    * For each implementer, we create an actor.
    */
  private val types : List[String] = Implementers.instance.implementers.map(item=> item.actorType).distinct
  types.foreach { actorType =>
    val supervisor = actorSystem.actorOf(Props.create(classOf[GenericSupervisorActor],actorType),actorType)
    supervisor ! StartActorsCommand(Implementers.instance.implementers.filter(item => item.actorType == actorType),config)
  }

  actorSystem.scheduler.schedule(1 minute, 15 seconds, () => {
    AfthemHttpClient.closeStaleConnections()
  })

  val probeHttpActor : ActorRef = actorSystem.actorOf(Props[ProbeHttpActor],"probeHttpActor")

  /**
    * Spring application context
    */
  var springApplicationContext : ApplicationContext = null

  /**
    * Inits the AppContext with the Spring application context
    * @param springApplicationContext a Spring application context
    */
  def init(springApplicationContext : ApplicationContext) : Unit = this.springApplicationContext = springApplicationContext



}
