package com.apifortress.afthem.routing

import com.apifortress.afthem.Parsers
import com.apifortress.afthem.config.{AfthemCache, Backend, Backends}
import com.apifortress.afthem.config.loaders.YamlConfigLoader
import org.junit.Test
import org.junit.Assert._

class RouterTests {

  val altBackend =
    """
      |prefix: '[^/]*/upstreams'
      |flow_id: default
      |upstreams:
      |    urls:
      |    - http://server1.example.com
      |    - http://server3.example.com
      |    probe:
      |      path: /foo
      |      timeout: 10 seconds
      |      count_up: 2
      |      count_down: 3
      |      interval: 1 hour
    """.stripMargin

  val altBackendNoProbes =
    """
      |prefix: '[^/]*/upstreams'
      |flow_id: default
      |upstreams:
      |    urls:
      |    - http://server1.example.com
      |    - http://server3.example.com
    """.stripMargin


  @Test
  def testCreateRouter() : Unit = {
    YamlConfigLoader.SUBPATH = "etc.test"
    AfthemCache.routersCache.clear()
    val backends = Backends.instance()
    val backend = backends.list().find(p => p.prefix=="[^/]*/upstreams" && p.headers == null).get
    val router = UpstreamsHttpRouters.getRouter(backend)
    assertEquals(backend.upstreams.probe,router.probe)
    assertEquals(backend.upstreams.urls,router.urls.map(p => p.url))
    assertEquals(backend.upstreams.urls(1),router.getNextUrl())
    assertEquals(backend.upstreams.urls(0),router.getNextUrl())
    assertEquals(backend.upstreams.urls(1),router.getNextUrl())
    assertEquals(backend.upstreams.urls(0),UpstreamsHttpRouters.getUrl(backend))
    assertEquals(router,UpstreamsHttpRouters.getRouter(backend))
    assertTrue(router.urls.find(p => p.url == "http://server2.example.com").isDefined)
    assertEquals(2,router.probe.countDown)

    val b2 = Parsers.parseYaml(altBackend,classOf[Backend])
    assertEquals(backend.getSignature(),b2.getSignature())
    assertNotEquals(backend.hashCode,b2.hashCode)

    val r2 = UpstreamsHttpRouters.getRouter(b2)
    assertEquals(r2,router)

    assertTrue(r2.urls.find(p => p.url == "http://server3.example.com").isDefined)
    assertTrue(r2.urls.find(p => p.url == "http://server1.example.com").isDefined)
    assertFalse(r2.urls.find(p => p.url == "http://server2.example.com").isDefined)

    assertEquals(3,r2.probe.countDown)

    val b3 = Parsers.parseYaml(altBackendNoProbes,classOf[Backend])
    val r3 = UpstreamsHttpRouters.getRouter(b3)
    assertEquals(r3,router)
    assertNull(r3.probe)


  }

}
