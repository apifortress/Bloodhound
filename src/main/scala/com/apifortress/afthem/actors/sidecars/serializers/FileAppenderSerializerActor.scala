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

package com.apifortress.afthem.actors.sidecars.serializers

import java.io.{File, FileOutputStream}
import java.nio.charset.StandardCharsets

import com.apifortress.afthem.Metric
import com.apifortress.afthem.messages.WebParsedResponseMessage

import scala.collection.mutable

class FileAppenderSerializerActor(phaseId : String) extends AbstractSerializerActor(phaseId : String)  {

  val outputStreams = mutable.HashMap.empty[String,FileOutputStream]

  override def receive: Receive = {
    case msg : WebParsedResponseMessage =>
      val m = new Metric
      val filename = getPhase(msg).getConfigString("filename")
      getOutputStream(filename).write((serialize(msg)+"\n").getBytes(StandardCharsets.UTF_8))
      metricsLog.debug(m.toString())
  }

  private def getOutputStream(filename : String): FileOutputStream ={
    val streamOption = outputStreams.get(filename)
    if(streamOption.isDefined)
      return streamOption.get
    val outputStream = new FileOutputStream(new File(filename))
    outputStreams.put(filename,outputStream)
    return outputStream
  }

  override def postStop(): Unit = {
    super.postStop()
    outputStreams.foreach( os => os._2.close())
  }
}
