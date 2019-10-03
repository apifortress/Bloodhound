package com.apifortress.afthem.actors.probing

import akka.actor.{ActorSystem, Props}
import com.apifortress.afthem.config.{AfthemCache, Backends, Probe}
import com.apifortress.afthem.config.loaders.YamlConfigLoader
import com.apifortress.afthem.routing.{RoutedUrl, UpstreamsHttpRouters}
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

  @Test
  def testPositiveProbing() : Unit = {
    implicit val system = ActorSystem()
    val url = new RoutedUrl("https://www.google.com",
                              new Probe("/",200,"GET","5 seconds","5 minutes",2,2))

    val actor = system.actorOf(Props(new ProbeHttpActor()))
    actor ! url
    actor ! url
    Thread.sleep(5000)
    assertTrue(url.upStatus)
    val url2 = new RoutedUrl("https://google.com",
      new Probe("",200,"GET","5 seconds","5 minutes",2,2))
    actor ! url2
    actor ! url2
    Thread.sleep(5000)
    assertFalse(url2.upStatus)


  }
}
