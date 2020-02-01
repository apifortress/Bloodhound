package com.apifortress.afthem.exceptions

import com.apifortress.afthem.messages.BaseMessage

class AfthemSevereException(override val message : BaseMessage, override val comment : String)
                                                                  extends AfthemFlowException(message,comment)
