package com.apifortress.afthem.actors.proxy

import com.apifortress.afthem.config.Backend
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage}
import org.apache.http.client.entity.GzipDecompressingEntity
import org.apache.http.message.BasicHeader
import org.apache.http.{Header, HttpEntity}
import org.junit.Test
import org.junit.Assert._
import org.mockito.Mockito._

import scala.collection.mutable

class UpstreamHttpActorTests {

  @Test
  def testExtractUpstream() : Unit = {
    val backend = mock(classOf[Backend])
    when(backend.upstream).thenReturn("http://foo.com/bar")
    val message = new WebParsedRequestMessage(null,backend,null,null,null,
                                                mutable.HashMap.empty[String,Any])
    val upstream = UpstreamHttpActor.extractUpstream(message)
    assertEquals("http://foo.com/bar",upstream)
  }

  @Test
  def testExtractUpstreamWithReplaceUpstream() : Unit = {
    val backend = new Backend(null,null,null,"http://foo.com/bar")
    val message = new WebParsedRequestMessage(null,backend,null,null,null,
                                                mutable.HashMap("__replace_upstream"->"http://bar.com/foo"))
    val upstream = UpstreamHttpActor.extractUpstream(message)
    assertEquals("http://bar.com/foo",upstream)
  }

  @Test
  def testWrapGzipEntityIfNeeded() : Unit = {
    val entity = mock(classOf[HttpEntity])
    val contentEncodingHeader = mock(classOf[Header])
    when(entity.getContentEncoding).thenReturn(new BasicHeader("content-encoding","gzip"))
    val entity2 = UpstreamHttpActor.wrapGzipEntityIfNeeded(entity)
    assertTrue( entity2.isInstanceOf[GzipDecompressingEntity])
    when(entity.getContentEncoding).thenReturn(new BasicHeader("content-encoding","identity"))
    val entity3 = UpstreamHttpActor.wrapGzipEntityIfNeeded(entity)
    assertTrue(entity3.isInstanceOf[HttpEntity])
  }
}
