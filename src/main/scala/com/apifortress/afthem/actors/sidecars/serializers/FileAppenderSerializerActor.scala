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

import com.apifortress.afthem.{AfthemResponseSerializer, Metric}
import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.messages.WebParsedResponseMessage

import scala.collection.mutable

/**
  * Actor that serializes a full HTTP conversation to file
  * @param phaseId the ID of the phase
  */
class FileAppenderSerializerActor(phaseId : String) extends AbstractAfthemActor(phaseId : String)  {

  /**
    * We retain all output streams we write to
    */
  private val outputStreams = mutable.HashMap.empty[String,FileOutputStream]

  override def receive: Receive = {
    case msg : WebParsedResponseMessage =>
      val m = new Metric
      val filename = getPhase(msg).getConfigString("filename")
      getOutputStream(filename).write((AfthemResponseSerializer.serialize(msg)+"\n").getBytes(StandardCharsets.UTF_8))
      metricsLog.debug(m.toString())
  }

  /**
    * Creates and/or returns an output stream for a given file name
    * @param filename a file name
    * @return an output stream for the given file name
    */
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
