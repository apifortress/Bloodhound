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
package com.apifortress.afthem.actors.essentials

import java.io.InputStream

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.Backend
import com.apifortress.afthem.messages.{ExceptionMessage, HttpWrapper, WebParsedRequestMessage, WebParsedResponseMessage}
import com.apifortress.afthem.{Metric, ReqResUtil, UriUtil}
import org.apache.http.HttpResponse
import org.apache.http.client.methods._
import org.apache.http.concurrent.FutureCallback
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor
import org.apache.http.util.EntityUtils

object DownloaderActor {

  val ioReactor = new DefaultConnectingIOReactor()
  val connectionManager = new PoolingNHttpClientConnectionManager(ioReactor)
  val httpClient : CloseableHttpAsyncClient = HttpAsyncClients.custom().setConnectionManager(connectionManager).build()
  httpClient.start()

}

/**
  * The actor taking care of retrieving the resource from the origin
  */
class DownloaderActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {


  override def receive: Receive = {
    case msg : WebParsedRequestMessage => {
      val m = new Metric
      val discardHeaders = getPhase(msg).getConfigList("discard_headers")

      val upstreamUrl = UriUtil.determineUpstreamUrl(msg.request.url, msg.backend)
      val httpReq: HttpUriRequest = createRequest(upstreamUrl, msg.request, discardHeaders)

      DownloaderActor.httpClient.execute(httpReq, new FutureCallback[HttpResponse] {
        override def completed(response: HttpResponse): Unit = {
          val entity = response.getEntity
          val inputStream = entity.getContent

          val wrapper = createResponseWrapper(response, inputStream)
          wrapper.url = upstreamUrl

          EntityUtils.consumeQuietly(entity)
          inputStream.close()

          val message = new WebParsedResponseMessage(wrapper, msg.request, msg.backend, msg.flow, msg.deferredResult, msg.date, msg.meta)

          forward(message)
        }

        override def failed(e: Exception): Unit = {

        }

        override def cancelled(): Unit = {}
      })
      log.debug(m.toString())

    }


  }

  private def createRequest(url: String, wrapper: HttpWrapper, discardHeaders : List[String]): HttpUriRequest = {
    val request = wrapper.method match {
      case "GET" =>  new HttpGet(url)
      case "POST" => new HttpPost(url)
      case "PUT" =>  new HttpPut(url)
      case "DELETE" => new HttpDelete(url)
      case "PATCH" =>  new HttpPatch(url)
      case _ =>  null
    }
    if(wrapper.payload != null && request.isInstanceOf[HttpEntityEnclosingRequestBase])
      request.asInstanceOf[HttpEntityEnclosingRequestBase].setEntity(new ByteArrayEntity(wrapper.payload))

    wrapper.headers.foreach(header =>
      if(!discardHeaders.contains(header._1.toLowerCase))
        request.setHeader(header._1,header._2)
    )

    return request
  }

  private def createResponseWrapper(response : HttpResponse, inputStream: InputStream): HttpWrapper = {
    val wrapper = new HttpWrapper
    wrapper.status = response.getStatusLine.getStatusCode
    val headersInfo = ReqResUtil.parseHeaders(response)
    wrapper.headers = headersInfo._1
    wrapper.payload = ReqResUtil.readPayload(inputStream,headersInfo._2.get("content-length"))
    return wrapper
  }
}
