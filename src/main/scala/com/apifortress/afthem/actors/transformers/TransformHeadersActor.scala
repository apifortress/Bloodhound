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

import com.apifortress.afthem.{Metric, SpelEvaluator}
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.beans.HttpWrapper
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage, WebParsedResponseMessage}

class TransformHeadersActor(phaseId : String) extends AbstractAfthemActor(phaseId) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>
      val m = new Metric
      perform(msg,msg.request)
      forward(msg)
      log.debug(m.toString())

    case msg : WebParsedResponseMessage =>
      val m = new Metric
      perform(msg,msg.response)
      forward(msg)
      log.debug(m.toString())
  }

  def perform(msg : BaseMessage, wrapper : HttpWrapper): Unit ={
    val headersToRemove = parseConfigHeaders(msg,getPhase(msg).getConfigListMap("remove_headers"))
    removeHeaders(wrapper,headersToRemove)
    val headersToAdd = parseConfigHeaders(msg,getPhase(msg).getConfigListMap("add_headers"))
    addHeaders(wrapper,headersToAdd)
  }

  def parseConfigHeaders(msg: BaseMessage, headers : List[Map[String,Any]]) : List[(String Tuple2 String)] = {
    return headers.map { item =>
      val headerName : String = item.get("name").get.asInstanceOf[String]
      val evaluated : Boolean = item.get("evaluated").getOrElse(false).asInstanceOf[Boolean]
      val data : String = item.get("value").getOrElse(null).asInstanceOf[String]
      val result = if (evaluated)
                    SpelEvaluator.evaluate(data,Map[String,Any]("msg" -> msg)).asInstanceOf[String]
                   else data
      (headerName,result)
    }
  }

  def addHeaders(wrapper : HttpWrapper, headers : List[(String Tuple2 String)])= {
    wrapper.headers = wrapper.headers++headers
  }

  def removeHeaders(wrapper: HttpWrapper, headers: List[(String Tuple2 String)]) = {
    val names= headers.map(h => h._1)
    wrapper.headers = wrapper.headers.filter( item => !names.contains(item._1))
  }

}
