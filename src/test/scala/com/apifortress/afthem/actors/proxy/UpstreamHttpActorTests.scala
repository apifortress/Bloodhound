/**
  * Copyright 2019 API Fortress
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  * @author Simone Pezzano
  */
package com.apifortress.afthem.actors.proxy

import java.util.Date

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.apifortress.afthem.{Metric, ReqResUtil}
import com.apifortress.afthem.config.{Backend, Phase}
import com.apifortress.afthem.messages.beans.HttpWrapper
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage, WebParsedResponseMessage}
import org.apache.commons.io.IOUtils
import org.apache.http.client.entity.GzipDecompressingEntity
import org.apache.http.client.methods.HttpPost
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
    val res = IOUtils.toString(apacheRequest.asInstanceOf[HttpPost].getEntity.getContent,ReqResUtil.CHARSET_UTF8)
    assertEquals("FOOBAR",res)
  }

  @Test
  def testActor() : Unit = {
    implicit val system = ActorSystem()
    val probe = TestProbe()
    //probe.expectMsg(30 seconds, classOf[WebParsedRequestMessage])
    val actor = system.actorOf(Props(new UpstreamHttpActor("abc") {
      override def forward(msg: BaseMessage): Unit = {
        probe.ref ! msg
      }

      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc","next")
      }
    }))
    val request = new HttpWrapper("https://foo.com",-1,"GET")
    val backend = new Backend(null,"foo.com",Map.empty[String,String],"https://www.google.com")
    val message = new WebParsedRequestMessage(request,backend,null,null)
    message.meta.put("__process_start",new Metric().time().toLong)
    message.meta.put("__start",new Metric().time().toLong)
    actor ! message
    val response = probe.expectMsgClass(classOf[WebParsedResponseMessage])
    assertTrue(response.response.getHeader("content-type").contains("html"))
    Thread.sleep(500)
    system.terminate()

  }

}
