package com.apifortress.afthem

import javax.servlet.http.HttpServletRequest
import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.BoundedInputStream
import org.apache.http.HttpResponse
import scala.collection.mutable
import java.io.InputStream

object ReqResUtil {

  val HEADER_CONTENT_LENGTH : String = "content-length"
  val HEADER_CONTENT_TYPE : String = "content-type"

  /**
    * Parses servlet headers into a list of tuples and collects a map of interesting headers
    * @param request an HttpServletRequest to parse the headers from
    * @return a tuple made up of (list of headers, interesting headers)
    */
  def parseHeaders(request: HttpServletRequest) : (List[(String Tuple2 String)],Map[String,Any]) = {
    val headers = new mutable.MutableList[String Tuple2 String]
    val interestingHeaders = new mutable.HashMap[String,Any]

    val headerNames = request.getHeaderNames
    while(headerNames.hasMoreElements){
      val headerName = headerNames.nextElement()
      headerName match {
        case HEADER_CONTENT_LENGTH =>
          interestingHeaders.put(HEADER_CONTENT_LENGTH,request.getHeader(HEADER_CONTENT_LENGTH).toInt)
        case HEADER_CONTENT_TYPE =>
          interestingHeaders.put(HEADER_CONTENT_TYPE,request.getHeader(HEADER_CONTENT_TYPE))
        case _ => {}
      }
      headers+=new Tuple2(headerName,request.getHeader(headerName))
    }
    return (headers.toList,interestingHeaders.toMap)
  }

  /**
    * Parses response headers into a list of tuples and collects a map of interesting headers
    * @param response an HttpResponse object
    * @return a tuple made up of (list of headers, interesting headers)
    */
  def parseHeaders(response: HttpResponse) : (List[(String Tuple2 String)],Map[String,Any]) = {
    val headers = new mutable.MutableList[String Tuple2 String]
    val interestingHeaders = new mutable.HashMap[String,Any]
    response.getAllHeaders.foreach{ header =>
      headers+=new Tuple2(header.getName,header.getValue)
      header.getName match {
        case HEADER_CONTENT_LENGTH =>
          interestingHeaders.put(HEADER_CONTENT_LENGTH, header.getValue.toInt)
        case HEADER_CONTENT_TYPE =>
          interestingHeaders.put(HEADER_CONTENT_TYPE, header.getValue)
        case _ => {}
      }
    }
    return (headers.toList,interestingHeaders.toMap)
  }

  /**
    * Reads a payload from an input stream. It will conform to what's told in the content-length header,
    * if present, or continue to EOF
    * @param inputStream the input stream to read from
    * @param contentLength an optional content-length header value
    * @return a byte array
    */
  def readPayload(inputStream: InputStream, contentLength: Option[Any]) : Array[Byte] = {
    val boundedInputStream = new BoundedInputStream(inputStream)
    var data :Array[Byte] = null
    try {
      if (contentLength.isDefined)
        // If content-length is defined, then we conform to it
        data = IOUtils.readFully(boundedInputStream, contentLength.get.asInstanceOf[Int])
      else
        // Otherwise we read to EOF
        data = IOUtils.toByteArray(boundedInputStream)
    }finally {
      // We want to make sure we're closing the streams
      boundedInputStream.close()
      inputStream.close()
    }
    return data
  }
}
