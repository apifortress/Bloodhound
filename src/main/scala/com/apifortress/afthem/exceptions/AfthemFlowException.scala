package com.apifortress.afthem.exceptions

import com.apifortress.afthem.messages.BaseMessage

/**
  * An exception that may happen during the main flow. This exception is meant to carry the necessary items to respond
  * to the requesting agent
  * @param message the message that was being processed
  * @param comment an optional comment
  */
class AfthemFlowException(val message : BaseMessage, val comment : String) extends Exception(comment)