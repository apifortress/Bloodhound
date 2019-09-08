package com.apifortress.afthem.actors.proxy

import java.util.Date

import com.apifortress.afthem.config.{Backend, Phase}
import com.apifortress.afthem.messages.WebParsedRequestMessage
import com.apifortress.afthem.messages.beans.HttpWrapper
import org.apache.commons.io.IOUtils
import org.apache.http.client.entity.GzipDecompressingEntity
import org.apache.http.client.methods.{HttpPost, HttpUriRequest}
import org.apache.http.message.BasicHeader
import org.apache.http.{Header, HttpEntity}
import org.junit.Assert._
import org.junit.Test
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

  @Test
  def testCreateRequest() : Unit = {
    val backend = mock(classOf[Backend])
    val phase = new Phase("foo","bar")
    when(backend.upstream).thenReturn("http://foo.com/bar")
    val request = new HttpWrapper("http://foo.com",-1,"POST",
                                  List[com.apifortress.afthem.messages.beans.Header](new com.apifortress.afthem.messages.beans.Header("foo","bar"))
                                  ,"FOOBAR".getBytes())
    val msg = new WebParsedRequestMessage(request, backend,null,null, new Date(), null)
    val apacheRequest = UpstreamHttpActor.createRequest(msg,phase)
    assertEquals("POST",apacheRequest.getMethod)
    assertEquals("http://foo.com",apacheRequest.getURI.toString)
    assertTrue(apacheRequest.getAllHeaders.find(it => it.getName == "foo").isDefined)
    val res = IOUtils.toString(apacheRequest.asInstanceOf[HttpPost].getEntity.getContent,"UTF-8")
    assertEquals("FOOBAR",res)
  }
}
