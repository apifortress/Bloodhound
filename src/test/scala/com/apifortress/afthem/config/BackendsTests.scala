package com.apifortress.afthem.config

import java.io.{File, FileReader}

import com.apifortress.afthem.Parsers
import javax.servlet.http.HttpServletRequest
import org.mockito.Mockito._
import org.junit.Assert._
import org.junit.Test

class BackendsTests {

  @Test
  def testFindByRequest() : Unit = {
    val reader = new FileReader(new File("etc.test"+File.separator+"backends.yml"))
    val backends = Parsers.parseYaml(reader,classOf[Backends])
    reader.close()
    val request = mock(classOf[HttpServletRequest])
    when(request.getRequestURL).thenReturn(new StringBuffer("http://foobar.com:8080/any/thing"))
    val found = backends.findByRequest(request).get
    assertEquals("[^/]*/any",found.prefix)
  }
  @Test
  def testFindByRequestWithHeader() : Unit = {
    val reader = new FileReader(new File("etc.test"+File.separator+"backends.yml"))
    val backends = Parsers.parseYaml(reader,classOf[Backends])
    reader.close()
    val request = mock(classOf[HttpServletRequest])
    when(request.getRequestURL).thenReturn(new StringBuffer("http://foobar.com:8080/only/with/header"))
    when(request.getHeader("x-my-header")).thenReturn("mastiff")
    val found = backends.findByRequest(request).get
    assertEquals("[^/]*/only/with/header",found.prefix)
  }
}
