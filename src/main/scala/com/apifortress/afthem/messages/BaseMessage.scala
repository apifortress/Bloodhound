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

import java.util.Date

import com.apifortress.afthem.config.{Backend, Flow}
import com.apifortress.afthem.messages.beans.AfthemResult

import scala.collection.mutable

/**
  * The base of all messages
  * @param backend the backend configuration for this transaction
  * @param flow the flow configuration for this transaction
  * @param deferredResult the deferred result to report to once the data is ready
  * @param date the date the message was created (defaults to new date)
  * @param meta generic metadata
  */
abstract class BaseMessage(val backend : Backend,
                  val flow: Flow,
                  val deferredResult: AfthemResult,
                  val date : Date = new Date(),
                  val meta : mutable.HashMap[String,Any] = new mutable.HashMap[String,Any]()) {

  /**
    * Shallow-clone the message
    * @param dropDeferredResult if true, the deferred result won't be copied over
    * @return a shallow clone of the message
    */
  def shallowClone(dropDeferredResult : Boolean): BaseMessage = {
    throw new IllegalStateException("Cannot clone abstract base class")
  }
}