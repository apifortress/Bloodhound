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

package com.apifortress.afthem.actors.transformers

import com.apifortress.afthem.Metric
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.EvalNameValue
import com.apifortress.afthem.messages.beans.{Header, HttpWrapper}
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage, WebParsedResponseMessage}

class TransformHeadersActor(phaseId : String) extends AbstractAfthemActor(phaseId) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>
      val m = new Metric
      perform(msg,msg.request)
      forward(msg)
      metricsLog.debug(m.toString())

    case msg : WebParsedResponseMessage =>
      val m = new Metric
      perform(msg,msg.response)
      forward(msg)
      metricsLog.debug(m.toString())
  }

  def perform(msg : BaseMessage, wrapper : HttpWrapper): Unit ={
    val headersToRemove = parseConfigHeaders(msg,getPhase(msg).getConfigListEvalNameValue("remove"))
    removeHeaders(wrapper,headersToRemove)
    val headersToAdd = parseConfigHeaders(msg,getPhase(msg).getConfigListEvalNameValue("add"))
    addHeaders(wrapper,headersToAdd)
    val headersToSet = parseConfigHeaders(msg,getPhase(msg).getConfigListEvalNameValue("set"))
    setHeaders(wrapper,headersToSet)
  }

  def parseConfigHeaders(msg: BaseMessage, headers : List[EvalNameValue]) : List[Header] = {
    return headers.map { item =>
      val result = item.evaluateIfNeeded(Map[String,Any]("msg" -> msg)).asInstanceOf[String]
      new Header(item.name,result)
    }
  }

  def addHeaders(wrapper : HttpWrapper, headers : List[Header]) : Unit = {
    wrapper.headers = wrapper.headers++headers
  }

  def removeHeaders(wrapper: HttpWrapper, headers: List[Header]): Unit = {
    val names= headers.map(h => h.key)
    wrapper.removeHeaders(names)
  }

  def setHeaders(wrapper: HttpWrapper, headers: List[Header]): Unit = {
    removeHeaders(wrapper,headers)
    addHeaders(wrapper,headers)
  }

}
