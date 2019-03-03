package com.apifortress.afthem.messages

import com.apifortress.afthem.config.Implementer
import com.typesafe.config.Config

case class StartActorsCommand(implementers : List[Implementer], config : Config)