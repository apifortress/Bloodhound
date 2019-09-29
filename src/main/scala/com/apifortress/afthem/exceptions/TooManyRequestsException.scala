package com.apifortress.afthem.exceptions

import com.apifortress.afthem.messages.WebParsedRequestMessage

case class TooManyRequestsException(override val message: WebParsedRequestMessage)
                                      extends AfthemFlowException(message,"Too many requests")
