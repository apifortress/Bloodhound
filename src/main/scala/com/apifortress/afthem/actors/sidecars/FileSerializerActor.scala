package com.apifortress.afthem.actors.sidecars

import com.apifortress.afthem.actors.AbstractAfthemActor

class FileSerializerActor(phaseId : String) extends AbstractAfthemActor(phaseId : String) {

  override def receive: Receive = {
    case _ => println("doh")
  }
}
