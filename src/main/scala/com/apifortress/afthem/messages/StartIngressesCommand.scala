package com.apifortress.afthem.messages

import com.apifortress.afthem.config.Ingress
import com.typesafe.config.Config

case class StartIngressesCommand(val ingresses : List[Ingress], val config: Config)
