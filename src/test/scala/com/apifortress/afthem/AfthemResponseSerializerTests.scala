package com.apifortress.afthem

import com.apifortress.afthem.messages.WebParsedResponseMessage
import com.apifortress.afthem.messages.beans.{Header, HttpWrapper}
import org.junit.Test
import org.junit.Assert._

import scala.collection.mutable

class AfthemResponseSerializerTests {


  @Test
  def testSerialization() : Unit = {
    val request = new HttpWrapper("http://foo.com",-1,"GET",
                            List[Header](new Header("foo","bar"),
                                         new Header("foo2","bar2")),Array.emptyByteArray,"127.0.0.1",
          "UTF-8")
    val response = new HttpWrapper("http://foo.com",200,"GET",
                                    List[Header](new Header("foo","bar"),
                                                 new Header("foo2","bar2"),
                                                 new Header("content-type","application/json")),"foobar".getBytes(),"127.0.0.1",
                              "UTF-8")
    val webResponse = new WebParsedResponseMessage(response,request,null,null,null)
    val expectation = Parsers.parseJSON("{\"request\":{\"querystring\":{},\"request_uri\":\"http://foo.com\",\"uri\":\"\",\"method\":\"GET\",\"size\":0,\"body\":\"---BINARY---\",\"headers\":{\"foo\":\"bar\"}},\"download_time\":null,\"started_at\":1567938240649,\"response\":{\"size\":6,\"body\":\"foobar\",\"status\":200,\"headers\":{\"content-type\":\"application/json\",\"foo\":\"bar\"}},\"client_ip\":\"127.0.0.1\"}",classOf[mutable.Map[String,Any]])
    val result = Parsers.parseJSON(AfthemResponseSerializer.serialize(webResponse,List("foo2"),List("foo2")),classOf[mutable.Map[String,Any]])

    expectation.remove("started_at")
    result.remove("started_at")
    assertEquals(expectation,result)
  }
}
