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
package com.apifortress.afthem.messages

/**
  * A message to carry an exception back to the requesting agent
  * @param exception the exception
  * @param status the status to return
  * @param message the message
  */
class ExceptionMessage(val exception: Exception,
                       val status: Int,
                       val message: BaseMessage)
    extends BaseMessage(message.backend, message.flow, message.deferredResult, message.date, message.meta) {

  /**
    * Will respond to the requesting agent with the given content type
    * @param contentType the content type, typically the value of the accept header
    */
  def respond(contentType : String = "application/json"): Unit = {
    if(message.deferredResult != null)
      deferredResult.setData(exception,status, contentType, message)
  }
}

