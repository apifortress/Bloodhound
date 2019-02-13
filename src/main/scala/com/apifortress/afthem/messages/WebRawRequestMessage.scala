package com.apifortress.afthem.messages

import javax.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.async.DeferredResult

case class WebRawRequestMessage(request: HttpServletRequest,
                                deferredResult: DeferredResult[ResponseEntity[Array[Byte]]]) extends BaseMessage
