package com.apifortress.afthem.exceptions

import com.apifortress.afthem.messages.BaseMessage

class AfthemFlowException(val message : BaseMessage, val comment : String) extends Exception(comment)