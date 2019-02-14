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

import java.io.File
import java.nio.charset.StandardCharsets

import com.apifortress.afthem.Metric
import com.apifortress.afthem.messages.WebParsedResponseMessage
import org.apache.commons.io.FileUtils

class FileAppenderSerializerActor(phaseId : String) extends AbstractSerializerActor(phaseId : String)  {

  override def receive: Receive = {
    case msg : WebParsedResponseMessage =>
      val m = new Metric
      val filename = getPhase(msg).getConfigString("filename")
      val file = new File(filename)
      FileUtils.write(file,serialize(msg)+"\n",StandardCharsets.UTF_8,true)
      log.debug(m.toString())
  }
}
