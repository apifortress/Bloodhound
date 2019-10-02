package com.apifortress.afthem.actors.probing

import akka.actor.{ActorSystem, Props}
import com.apifortress.afthem.config.{AfthemCache, Backends}
import com.apifortress.afthem.config.loaders.YamlConfigLoader
import com.apifortress.afthem.routing.UpstreamsHttpRouters
import org.junit.Assert._
import org.junit.Test

class ProbeHttpActorTests {

  @Test
  def testProbe() : Unit = {
    YamlConfigLoader.SUBPATH = "etc.test"
    AfthemCache.routersCache.clear()
    AfthemCache.configCache.clear()
    val backends = Backends.instance()
    val backend = backends.list().find(p => p.prefix=="[^/]*/upstreams" && p.headers == null).get
    val router = UpstreamsHttpRouters.getRouter(backend)
    implicit val system = ActorSystem()

    val actor = system.actorOf(Props(new ProbeHttpActor()))
    actor ! router
    actor ! router
    actor ! router
    Thread.sleep(5000)
    router.urls.foreach(p => assertFalse(p.upStatus))



  }
}
