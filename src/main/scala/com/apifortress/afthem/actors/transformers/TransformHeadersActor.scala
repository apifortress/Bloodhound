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
import com.apifortress.afthem.exceptions.AfthemFlowException
import com.apifortress.afthem.messages.beans.{Header, HttpWrapper}
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage, WebParsedResponseMessage}


/**
  * Companion object for TransformHeadersActor
  */
object TransformHeadersActor {
  /**
    * Adds a list of headers to an HttpWrapper
    *
    * @param wrapper an HttpWrapper
    * @param headers a list of headers
    */
  def addHeaders(wrapper : HttpWrapper, headers : List[Header]) : Unit = {
    wrapper.headers = wrapper.headers++headers
  }

  /**
    * Removes a list of headers from an HttpWrapper
    *
    * @param wrapper an HttpWrapper
    * @param headers a list of headers
    */
  def removeHeaders(wrapper: HttpWrapper, headers: List[Header]): Unit = {
    val names= headers.map(h => h.key)
    wrapper.removeHeaders(names)
  }

  /**
    * Sets a list of headers to an HttpWrapper
    *
    * @param wrapper an HttpWrapper
    * @param headers a list of headers
    */
  def setHeaders(wrapper: HttpWrapper, headers: List[Header]): Unit = {
    removeHeaders(wrapper,headers)
    addHeaders(wrapper,headers)
  }

  /**
    * Evaluates a list of EvalNameValue into headers
    *
    * @param msg the message, used as the scope of the evaluation
    * @param headers a list of EvalNameValue
    * @return a list of headers
    */
  def evaluateConfigHeaders(msg: BaseMessage, headers : List[EvalNameValue]) : List[Header] = {
    return headers.map { item =>
      val result = item.evaluateIfNeeded(Map[String,Any]("msg" -> msg)).asInstanceOf[String]
      new Header(item.name,result)
    }
  }
}
/**
  * Actor taking care of transforming request or response headers
  *
  * @param phaseId the ID of the phase
  */
class TransformHeadersActor(phaseId : String) extends AbstractAfthemActor(phaseId) {

  override def receive: Receive = {
    case msg : WebParsedRequestMessage =>
      try{
        val m = new Metric
        perform(msg,msg.request)
        forward(msg)
        metricsLog.debug(m.toString())
      }catch {
        case e : Exception =>
          log.error("Exception during the transform headers operation",e  )
          throw new AfthemFlowException(msg,e.getMessage)
      }

    case msg : WebParsedResponseMessage =>
      try{
        val m = new Metric
        perform(msg,msg.response)
        forward(msg)
        metricsLog.debug(m.toString())
      }catch {
        case e : Exception =>
          log.error("Exception during the transform headers operation",e)
          throw new AfthemFlowException(msg,e.getMessage)
      }
  }

  /**
    * Performs the transformation on a provided HttpWrapper
    *
    * @param msg a message
    * @param wrapper the wrapper to alter
    */
  def perform(msg : BaseMessage, wrapper : HttpWrapper): Unit ={
    val headersToRemove = TransformHeadersActor.evaluateConfigHeaders(msg,getPhase(msg).getConfigListEvalNameValue("remove"))
    TransformHeadersActor.removeHeaders(wrapper,headersToRemove)
    val headersToAdd = TransformHeadersActor.evaluateConfigHeaders(msg,getPhase(msg).getConfigListEvalNameValue("add"))
    TransformHeadersActor.addHeaders(wrapper,headersToAdd)
    val headersToSet = TransformHeadersActor.evaluateConfigHeaders(msg,getPhase(msg).getConfigListEvalNameValue("set"))
    TransformHeadersActor.setHeaders(wrapper,headersToSet)
  }





}
