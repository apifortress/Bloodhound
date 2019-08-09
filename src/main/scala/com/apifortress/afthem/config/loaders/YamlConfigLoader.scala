package com.apifortress.afthem.config.loaders

import java.io.File

import com.apifortress.afthem.Parsers
import com.apifortress.afthem.config.Backends.log
import com.apifortress.afthem.config._

import scala.io.Source

class YamlConfigLoader(params: Map[String,Any] = null) extends TConfigLoader {

  def loadAfthemRootConf() : RootConfigConf = {
    return parse[RootConfigConf]("afthem.yml", classOf[RootConfigConf])
  }

  override def loadBackends(): Backends = {
    var instance : Backends = AfthemCache.configCache.get("backends").asInstanceOf[Backends]
    if (instance != null){
      log.debug("Backends loaded from cache")
      return instance
    }
    log.debug("Backends loaded from disk")
    instance = parse[Backends]("backends.yml", classOf[Backends])
    AfthemCache.configCache.put("backends", instance)
    return instance
  }

  override def loadFlow(id: String): Flow = {
    return parse[Flow]("flows"+File.separator+id+".yml",classOf[Flow])
  }

  override def loadImplementers(): Implementers = {
    return parse[Implementers]("implementers.yml",classOf[Implementers])
  }

  /**
    * Parses a configuration file in the etc/ directory
    * @param filename the filename we want to load
    * @param theClass the class of the configuration file
    * @tparam T the class of the configuration file
    * @return the parsed configuration file
    */
  private def parse[T](filename : String, theClass : Class[T]): T = {
    val reader = Source.fromFile("etc"+File.separator+filename).reader()
    val resp : T = Parsers.parseYaml[T](reader, theClass)
    reader.close()
    return resp
  }
}
