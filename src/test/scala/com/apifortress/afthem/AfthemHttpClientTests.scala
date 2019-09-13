package com.apifortress.afthem

import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.concurrent.FutureCallback
import org.junit.Assert._
import org.junit.Test

class AfthemHttpClientTests {

  @Test
  def testExecute() : Unit = {
    AfthemHttpClient.execute(new HttpGet("https://www.google.com"),new FutureCallback[HttpResponse] {
      override def completed(t: HttpResponse): Unit = {
        assertEquals(200,t.getStatusLine.getStatusCode)
      }

      override def failed(e: Exception): Unit = {}

      override def cancelled(): Unit = {}
    })
    Thread.sleep(500)
  }
}
