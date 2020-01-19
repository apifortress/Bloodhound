package com.apifortress.afthem.exceptions

import com.apifortress.afthem.messages.WebParsedRequestMessage

case class UnauthenticatedException(override val message: WebParsedRequestMessage)
                                      extends AfthemFlowException(message,"Request not authenticated")
