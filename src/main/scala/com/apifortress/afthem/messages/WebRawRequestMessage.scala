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
import javax.servlet.http.HttpServletRequest

import scala.collection.mutable


/**
  * A message containing a raw request
  * @param request a raw request
  * @param deferredResult the deferred result awaiting content to conclude the communication
  */
case class WebRawRequestMessage(request: HttpServletRequest,
                                override val backend: Backend,
                                override val  flow: Flow,
                                override val deferredResult: AfthemResult,
                                override val date: Date = new Date(),
                                override val meta: mutable.HashMap[String,Any] = mutable.HashMap.empty[String,Any])
      extends BaseMessage(backend, flow, deferredResult, date, meta)
