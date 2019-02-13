package com.apifortress.afthem.messages

import com.apifortress.afthem.config.Backend
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.async.DeferredResult
import java.util.Date

import scala.collection.mutable

case class WebParsedRequestMessage(request: HttpWrapper,
                                   deferredResult: DeferredResult[ResponseEntity[Array[Byte]]],
                                   override val date: Date, override val meta: mutable.HashMap[String,Any],
                                   var backendConfig: Backend = null) extends BaseMessage(date,meta)
