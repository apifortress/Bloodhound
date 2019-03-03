package com.apifortress.afthem.messages

import com.apifortress.afthem.config.Implementer
import com.typesafe.config.Config

/**
  * A command message to be sent to a generic supervistor actor to initiate actors creation
  * @param implementers the implementers to created
  * @param config the Akka config
  */
case class StartActorsCommand(implementers : List[Implementer], config : Config)