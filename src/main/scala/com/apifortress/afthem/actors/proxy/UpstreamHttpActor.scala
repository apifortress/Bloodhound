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

import java.io.{ByteArrayInputStream, InputStream}
import java.util.concurrent.TimeUnit

import com.apifortress.afthem._
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.exceptions.AfthemFlowException
import com.apifortress.afthem.messages.beans.HttpWrapper
import com.apifortress.afthem.messages.{BaseMessage, ExceptionMessage, WebParsedRequestMessage, WebParsedResponseMessage}
import com.apifortress.afthem.routing.UpstreamsHttpRouters
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.GzipDecompressingEntity
import org.apache.http.client.methods._
import org.apache.http.concurrent.FutureCallback
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.util.EntityUtils
import org.apache.http.{HttpEntity, HttpResponse}

/**
  * Companion object for the Upstream Http Actor
  */
object UpstreamHttpActor {

  /**
    * Request headers that need to be dropped before the request is performed
    */
  val DROP_HEADERS : List[String] = List("content-length","host")

  /**
    * If the entity declares itself as a GZIP entity, it gets wrapped into a GzipDecompressingEntity
    * @param entity an HttpEntity
    * @return a GzipDecompressingEntity, if the inbound entity declares itself as GZIP
    */
  def wrapGzipEntityIfNeeded(entity : HttpEntity) : HttpEntity = {
    if (entity.getContentEncoding != null && entity.getContentEncoding.getValue.toLowerCase.contains("gzip"))
      return new GzipDecompressingEntity(entity)
    return entity
  }

  /**
    * Creates an HTTP client request with the provided data
    * @param msg a WebParsedRequestMessage
    * @param phase the phase
    * @return an HttpUriRequest, ready to be executed
    */
  def createRequest(msg: WebParsedRequestMessage, phase : Phase): HttpUriRequest = {

    val wrapper = msg.request

    val discardHeaders = phase.getConfigList("discard_headers")

    val url = wrapper.getURL()

    val request = AfthemHttpClient.createBaseRequest(wrapper.method,url)

    val requestConfig = RequestConfig.custom().setConnectTimeout(phase.getConfigDurationAsMillis("connect_timeout",5000, TimeUnit.MILLISECONDS))
                                                .setSocketTimeout(phase.getConfigDurationAsMillis("socket_timeout",10000, TimeUnit.MILLISECONDS))
                                                .setRedirectsEnabled(phase.getConfigBoolean("redirects_enabled").getOrElse(false))
                                                .setMaxRedirects(phase.getConfigInt("max_redirects",5)).build()

    request.setConfig(requestConfig)

    if(wrapper.payload != null && request.isInstanceOf[HttpEntityEnclosingRequestBase])
      request.asInstanceOf[HttpEntityEnclosingRequestBase].setEntity(new ByteArrayEntity(wrapper.payload))

    copyHeadersToRequest(wrapper,discardHeaders,request)

    request
  }

  /**
    * Copies the headers from the wrapper to the HttpRequest and discards headers meant to be discarded
    * @param wrapper the wrapper
    * @param discardHeaders the headers meant to be discarded
    * @param httpRequest the HttpRequest
    */
  def copyHeadersToRequest(wrapper: HttpWrapper, discardHeaders : List[String], httpRequest: HttpRequestBase) : Unit = {
    wrapper.headers.foreach(header =>
      if(!discardHeaders.contains(header.key.toLowerCase) && !UpstreamHttpActor.DROP_HEADERS.contains(header.key.toLowerCase))
        httpRequest.setHeader(header.key,header.value)
    )
  }

  /**
    * Creates an HttpWrapper for the response
    *
    * @param requestWrapper the HttpWrapper of the request
    * @param response the response from the HTTP Client
    * @param inputStream the stream connected to the response body
    * @return the response HttpWrapper
    */
  def createResponseWrapper(requestWrapper: HttpWrapper, response : HttpResponse, inputStream: InputStream): HttpWrapper = {
    val headersInfo = ReqResUtil.parseHeaders(response)
    new HttpWrapper(requestWrapper.getURL(),
      response.getStatusLine.getStatusCode,
      requestWrapper.method,
      headersInfo._1,
      ReqResUtil.readPayload(inputStream,headersInfo._2.get(ReqResUtil.HEADER_CONTENT_LENGTH)),
      null,
      ReqResUtil.getCharsetFromResponse(response))
  }

  /**
    * Extracts the upstream url, based on the presence (or absence) of a __replace_upstream meta
    * @param msg the message
    * @return the upstream
    */
  def extractUpstream(msg : BaseMessage) : String = {
    msg.meta.getOrElse("__replace_upstream",
                        UpstreamsHttpRouters.getUrl(msg.backend)).asInstanceOf[String]
  }
}

/**
  * The actor taking care of retrieving the resource from the origin
  * @param phaseId the phase ID
  */
class UpstreamHttpActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>
      try {
        val m = new Metric
        val upstream = UpstreamHttpActor.extractUpstream(msg)

        /**
          * Upstream == null is a possibility. An null upstream is useful when you want to use AFtheM as a bypass with
          * the help of an actual forward proxy. In this case, the original request URL is used.
          */
        if(upstream!=null) {
          msg.request.setURL(UriUtil.determineUpstreamUrl(msg.request.uriComponents, upstream, msg.backend))
          msg.request.setHeader("host", msg.request.uriComponents.getHost)
        }
        val httpReq: HttpUriRequest = UpstreamHttpActor.createRequest(msg,getPhase(msg))

        metricsLog.info("Processing time: "+new Metric(msg.meta.get("__process_start").get.asInstanceOf[Long]))
        metricsLog.debug("Time to Upstream: "+new Metric(msg.meta.get("__start").get.asInstanceOf[Long]))
        AfthemHttpClient.execute(httpReq, new AfthemHttpCallback(msg,m))
        metricsLog.debug(m.toString())
      }catch {
        case e : Exception =>
          log.error("Error while making the upstream call", e)
          throw new AfthemFlowException(msg,e.getMessage)
      }
  }

  /**
    * The HTTP that gets called when the response content is ready
    * @param msg the request message
    * @param m the metric started at the beginning of the actor
    */
  class AfthemHttpCallback(msg : WebParsedRequestMessage, m : Metric) extends FutureCallback[HttpResponse] {
    override def completed(response: HttpResponse): Unit = {
      var inputStream : InputStream = null
      try {
        /**
          * Sometimes responses will contain no body. Empty bodies come in two flavors: empty or absent.
          * Absent means there's literally nothing in there, and in that case no entity exists at all.
          * Therefore we need to check whether an entity exists or not.
          */
        val wrapper = if(response.getEntity != null) {
          /*
           * Async HTTP Client does not support automatic gunzip of the content, therefore we need to read
           * the appropriate header and handle it manually.
           */
          val entity = UpstreamHttpActor.wrapGzipEntityIfNeeded(response.getEntity)

          inputStream = entity.getContent
          val wrapper = UpstreamHttpActor.createResponseWrapper(msg.request, response, inputStream)
          EntityUtils.consumeQuietly(entity)
          wrapper
        } else
        /**
          * Case where no entity is present, so entity will no be handled at all
          */
          UpstreamHttpActor.createResponseWrapper(msg.request, response, new ByteArrayInputStream(Array[Byte]()))

        val message = new WebParsedResponseMessage(wrapper, msg.request, msg.backend, msg.flow, msg.deferredResult, msg.date, msg.meta)
        metricsLog.info("Download time: " + m.toString())
        message.meta.put("__download_time", m.time())
        forward(message)
      }catch {
        case e: Exception =>
          getLog.debug("Error at upstream download", e)
          new ExceptionMessage(e, 502, msg).respond()
      } finally {
        if (inputStream != null)
          inputStream.close()
      }
    }

    override def failed(e: Exception): Unit = {
      getLog.debug("Error during upstream download",e)
      new ExceptionMessage(e,502,msg).respond()
    }

    override def cancelled(): Unit = {}
  }
}
