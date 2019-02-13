package com.apifortress.afthem.actors.essentials

import java.io.InputStream

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.Backend
import com.apifortress.afthem.messages.{ExceptionMessage, HttpWrapper, WebParsedRequestMessage, WebParsedResponseMessage}
import com.apifortress.afthem.{ReqResUtil, UriUtil}
import org.apache.http.HttpResponse
import org.apache.http.client.methods._
import org.apache.http.concurrent.FutureCallback
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor
import org.apache.http.util.EntityUtils

/**
  * Companion object because we want to leverage the ability to use connection pools across
  * multiple actors.
  */
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
      val upstreamUrl = UriUtil.determineUpstreamUrl(msg.request.url, msg.backendConfig)
      val httpReq: HttpUriRequest = createRequest(upstreamUrl, msg.request, msg.backendConfig)

      DownloaderActor.httpClient.execute(httpReq, new FutureCallback[HttpResponse] {
        override def completed(response: HttpResponse): Unit = {
          val entity = response.getEntity
          val inputStream = entity.getContent

          val wrapper = createResponseWrapper(response, inputStream)
          wrapper.url = upstreamUrl

          EntityUtils.consumeQuietly(entity)
          inputStream.close()

          val message = new WebParsedResponseMessage(wrapper, msg.request, msg.deferredResult,msg.date,msg.meta)

          forward(message)
        }

        override def failed(e: Exception): Unit = {
          selectNextActor() ! new ExceptionMessage(e)
        }

        override def cancelled(): Unit = {}
      })

    }


  }

  private def createRequest(url: String, wrapper: HttpWrapper, backendConfig: Backend): HttpUriRequest = {
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
      if(header._1 != "content-length")
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
