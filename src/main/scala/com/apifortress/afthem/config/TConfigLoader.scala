package com.apifortress.afthem.config

/**
  * Trait for config loaders
  */
trait TConfigLoader {

  /**
    * Loads backend definitions
    * @return an instance of Backends
    */
  def loadBackends() : Backends

  /**
    * Loads a flow definition
    * @param id the ID of the flow
    * @return an instance of Flow
    */
  def loadFlow(id : String) : Flow

  /**
    * Loads implementers
    * @return an instance of Implementers
    */
  def loadImplementers() :  Implementers

}
