package com.apifortress.afthem.actors

import java.io.File

import akka.actor.{ActorSelection, ActorSystem, Props}
import akka.routing.FromConfig
import com.apifortress.afthem.config.{Phase, Phases}
import com.typesafe.config.ConfigFactory


object AppContext {

  val phases = Phases.load()


  val config = ConfigFactory.parseFile(new File("etc"+File.separator+"application.conf"))
  val actorSystem : ActorSystem = ActorSystem.create("afthem",config)

  phases.phases.foreach{ item =>
    val theClass = Class.forName(item._2.className)
    val prop = Props.create(theClass,item._1)
    if(config.hasPath("akka.actor.deployment./"+item._1)){
      actorSystem.actorOf(FromConfig.props(prop), item._1)
    } else
      actorSystem.actorOf(prop, item._1)
  }

  def getActorByPath(path : String) : ActorSelection = actorSystem.actorSelection(path)

  def getActorById(id : String) : ActorSelection = getActorByPath(getPathById(id))

  def getActorByPhase(phase: Phase) : ActorSelection = getActorById(phase.id)

  def getActorByPhaseId(phaseId : String) : ActorSelection = getActorByPhase(Phases.load().getPhase(phaseId))

  def init() : Unit = {}

  def getPathById(id : String ) : String = "/user/"+id

}
