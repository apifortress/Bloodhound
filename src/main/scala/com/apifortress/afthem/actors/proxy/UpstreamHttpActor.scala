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

import java.io.InputStream

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.exceptions.AfthemFlowException
import com.apifortress.afthem.messages.beans.HttpWrapper
import com.apifortress.afthem.messages.{ExceptionMessage, WebParsedRequestMessage, WebParsedResponseMessage}
import com.apifortress.afthem.{Metric, ReqResUtil, UriUtil}
import org.apache.http.HttpResponse
import org.apache.http.client.methods._
import org.apache.http.concurrent.FutureCallback
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor
import org.apache.http.util.EntityUtils

object UpstreamHttpActor {

  val ioReactor = new DefaultConnectingIOReactor()
  val connectionManager = new PoolingNHttpClientConnectionManager(ioReactor)
  val httpClient : CloseableHttpAsyncClient = HttpAsyncClients.custom().disableCookieManagement().setConnectionManager(connectionManager).build()
  httpClient.start()

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
        val discardHeaders = getPhase(msg).getConfigList("discard_headers")

        msg.request.url = UriUtil.determineUpstreamUrl(msg.request.url, msg.backend)
        val httpReq: HttpUriRequest = createRequest(msg.request, discardHeaders)
        metricsLog.debug("Time to Request: "+new Metric(msg.meta.get("__start").get.asInstanceOf[Long]))
        UpstreamHttpActor.httpClient.execute(httpReq, new FutureCallback[HttpResponse] {
          override def completed(response: HttpResponse): Unit = {
            val entity = response.getEntity
            val inputStream = entity.getContent

            val wrapper = createResponseWrapper(msg.request, response, inputStream)

            EntityUtils.consumeQuietly(entity)
            inputStream.close()

            val message = new WebParsedResponseMessage(wrapper, msg.request, msg.backend, msg.flow, msg.deferredResult, msg.date, msg.meta)

            forward(message)
          }

          override def failed(e: Exception): Unit = {
            new ExceptionMessage(e,502,msg).respond()
          }

          override def cancelled(): Unit = {}
        })
        metricsLog.debug(m.toString())
      }catch {
        case e : Exception => throw new AfthemFlowException(msg,e.getMessage)
      }


  }

  /**
    * Creates an HTTP client request with the provided data
    *
    * @param wrapper the request wrapper
    * @param discardHeaders list of header names to discard
    * @return an HttpURiRequest
    */
  private def createRequest(wrapper: HttpWrapper, discardHeaders : List[String]): HttpUriRequest = {
    val request = wrapper.method match {
      case "GET" =>  new HttpGet(wrapper.url)
      case "POST" => new HttpPost(wrapper.url)
      case "PUT" =>  new HttpPut(wrapper.url)
      case "DELETE" => new HttpDelete(wrapper.url)
      case "PATCH" =>  new HttpPatch(wrapper.url)
      case _ =>  null
    }
    if(wrapper.payload != null && request.isInstanceOf[HttpEntityEnclosingRequestBase])
      request.asInstanceOf[HttpEntityEnclosingRequestBase].setEntity(new ByteArrayEntity(wrapper.payload))

    wrapper.headers.foreach(header =>
      if(!discardHeaders.contains(header.key.toLowerCase))
        request.setHeader(header.key,header.value)
    )

    request
  }

  /**
    * Creates an HttpWrapper for the response
    *
    * @param requestWrapper the HttpWrapper of the request
    * @param response the response from the HTTP Client
    * @param inputStream the stream connected to the response body
    * @return the response HttpWrapper
    */
  private def createResponseWrapper(requestWrapper: HttpWrapper, response : HttpResponse, inputStream: InputStream): HttpWrapper = {
    val headersInfo = ReqResUtil.parseHeaders(response)
    new HttpWrapper(requestWrapper.url,
      response.getStatusLine.getStatusCode,
      requestWrapper.method,
      headersInfo._1,
      ReqResUtil.readPayload(inputStream,headersInfo._2.get("content-length")))
  }
}
