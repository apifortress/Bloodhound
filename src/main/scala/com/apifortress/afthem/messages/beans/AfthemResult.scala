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

package com.apifortress.afthem.messages.beans

import com.apifortress.afthem.ResponseEntityUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.async.DeferredResult

/**
  * Our implementation of the deferred result
  * @param data
  */
class AfthemResult(data : HttpWrapper = null) extends DeferredResult[ResponseEntity[Array[Byte]]] {

  if(data != null)
    setData(data)

  def setData(data : HttpWrapper) : Unit = {
    setResult(ResponseEntityUtil.createEntity(data))
  }

  def setData(exception : Exception, status : Int) : Unit = {
    setResult(ResponseEntityUtil.createEntity(exception, status))
  }

}