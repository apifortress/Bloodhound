/*
 *   Copyright 2019 API Fortress
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   @author Simone Pezzano
 *
 */
package com.apifortress.afthem.exceptions

import com.apifortress.afthem.messages.WebParsedRequestMessage

/**
  * Exception to be triggered when too many requests reach a throttling check agent
  * @param message the message that was being processed
  */
case class TooManyRequestsException(override val message: WebParsedRequestMessage)
                                      extends AfthemFlowException(message,"Too many requests")
