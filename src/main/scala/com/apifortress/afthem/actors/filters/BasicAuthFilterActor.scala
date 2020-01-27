/*
 *   Copyright 2020 API Fortress
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
package com.apifortress.afthem.actors.filters

import java.io.{File, FileInputStream}
import java.util.Base64

import com.apifortress.afthem.actors.AbstractAfthemActor
import com.apifortress.afthem.config.AfthemCache
import com.apifortress.afthem.exceptions.UnauthenticatedException
import com.apifortress.afthem.messages.{ExceptionMessage, WebParsedRequestMessage}
import com.apifortress.afthem.{Metric, ReqResUtil}
import org.apache.commons.codec.digest.Md5Crypt
import org.apache.commons.io.IOUtils

/**
  * Companion object for BasicAuthFilterActor
  */
object BasicAuthFilterActor {
  /**
    * Extracts credentials from the request headers. If preconditions are not met, an "UNAUTHENTICATED" error
    * is send back.
    *
    * @param msg request message
    * @return an array containing username at position 0 and password at position 1
    */
  def extractCredentials(msg: WebParsedRequestMessage): Array[String] = {
    val authorization = msg.request.getHeader("authorization")
    if (authorization == null || !authorization.startsWith("Basic"))
      return null
    val auth64 = new String(Base64.getDecoder.decode(authorization.substring(5).trim))
    if (!auth64.contains(":"))
      return null
    return auth64.split(":")
  }

}

class BasicAuthFilterActor(phaseId: String) extends AbstractAfthemActor(phaseId: String) {

  override def receive: Receive = {
    case msg: WebParsedRequestMessage =>
      val m = new Metric()
      if(authenticate(msg))
        tellNextActor(msg)
      else {
        sendUnauthenticated(msg)
      }
      metricsLog.debug(m.toString())
  }

  def authenticate(msg: WebParsedRequestMessage): Boolean = {
    val credentials = BasicAuthFilterActor.extractCredentials(msg)
    if (credentials == null)
      return false
    val htpasswd = loadHtpasswd(msg)
    val theLine = htpasswd.lines.find(it => it.startsWith(credentials(0)))
    if (theLine.isDefined) {
      val password = theLine.get.split(":")(1)
      val success = comparePassword(credentials(1), password)
      if (success)
        msg.meta.put("user", credentials(0))
      return success
    }
    return false
  }

  def loadHtpasswd(msg: WebParsedRequestMessage): String = {
    val filename = getPhase(msg).getConfigString("filename", "htpasswd")
    val cachedData = AfthemCache.htpasswdsCache.get(filename)
    if (cachedData != null) {
      log.debug("Htpasswd from cache")
      return cachedData
    }
    val fis = new FileInputStream(new File(filename))
    val data = IOUtils.toString(fis, ReqResUtil.CHARSET_UTF8)
    fis.close()
    AfthemCache.htpasswdsCache.put(filename, data)
    return data
  }

  def comparePassword(password: String, hash: String): Boolean = {
    val salt = hash.split("\\$")(2)
    val hash2 = Md5Crypt.apr1Crypt(password, salt)
    return hash == hash2
  }

  def sendUnauthenticated(msg: WebParsedRequestMessage): Unit = {
    val exceptionMessage = new ExceptionMessage(new UnauthenticatedException(msg), 401, msg)
    tellSidecars(exceptionMessage)
    exceptionMessage.respond(ReqResUtil.extractAcceptFromMessage(msg, "application/json"))
  }

}
