package com.apifortress.afthem.config

import com.apifortress.afthem.config.loaders.YamlConfigLoader

object ConfigLoader extends TConfigLoader {

  private val rootConfig = new YamlConfigLoader().loadAfthemRootConf()

  val implementer : TConfigLoader = Class.forName(rootConfig.configLoader.className)
                                      .getDeclaredConstructor(classOf[Map[String,Any]])
                                      .newInstance(rootConfig.configLoader.params).asInstanceOf[TConfigLoader]

  override def loadBackends(): Backends = return implementer.loadBackends()

  override def loadFlow(id: String): Flow = return implementer.loadFlow(id)

  override def loadImplementers(): Implementers = return implementer.loadImplementers()
}
