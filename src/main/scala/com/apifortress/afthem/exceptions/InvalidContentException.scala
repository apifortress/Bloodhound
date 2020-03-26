package com.apifortress.afthem.exceptions

import com.apifortress.afthem.Parsers
import com.apifortress.afthem.messages.BaseMessage

class InvalidContentException(override val message : BaseMessage, details : List[String]) extends AfthemFlowException(message,"Request/Response is invalid") {

  override def toJSON(): String = {
    val msg = Map("status" -> "error",
                  "message"->"Invalid Content",
                  "details"-> details)
    return Parsers.serializeAsJsonString(msg)
  }

  override def toXML(): String = {
    val msg = Map("status" -> "error",
      "message"->"Invalid Content",
      "error"-> details)
    return Parsers.serializeAsXmlString(msg, "exception")
  }
}
