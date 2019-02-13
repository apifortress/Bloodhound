package com.apifortress.afthem.messages.log

import java.util.Date

case class InboundLogMessage(remoteIP: String, url: String, date: Date = new Date())
