package com.apifortress.afthem.messages

import java.util.Date

import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.async.DeferredResult

import scala.collection.mutable

case class WebParsedResponseMessage(response: HttpWrapper, request: HttpWrapper,
                                    deferredResult: DeferredResult[ResponseEntity[Array[Byte]]],
                                    override val date: Date, override val meta: mutable.HashMap[String,Any]) extends BaseMessage(date,meta)
