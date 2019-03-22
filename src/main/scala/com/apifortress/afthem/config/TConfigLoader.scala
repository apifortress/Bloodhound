package com.apifortress.afthem.config

trait TConfigLoader {

  def loadBackends() : Backends

  def loadFlow(id : String) : Flow

  def loadImplementers() :  Implementers

}
