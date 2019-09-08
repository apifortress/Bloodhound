package com.apifortress.afthem

import java.io.IOException

import org.junit.Test
import org.junit.Assert._

class ResponseEntityUtilTests {


  @Test
  def testCreateEntityForException() : Unit = {
    var entity = ResponseEntityUtil.createEntity(new IOException("foobar"),500)
    assertTrue(entity.getStatusCode.is5xxServerError())
    assertEquals("{ \"status\": \"error\", \"message\": \"IOException: foobar\"}\n",new String(entity.getBody))

    entity = ResponseEntityUtil.createEntity(new IOException("foobar"),500,"text/xml")
    assertEquals("<exception><status>error</status><message>IOException: foobar</message></exception>",
                  new String(entity.getBody))

    entity = ResponseEntityUtil.createEntity(new IOException("foobar"),500,"text/plain")
    assertEquals("status: error\nexception: IOException: foobar", new String(entity.getBody))
  }
}
