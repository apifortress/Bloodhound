package com.apifortress.afthem.messages.log

import java.util.Date

case class BackendLogMessage(url: String, status: Int, date: Date = new Date())
