package com.apifortress.afthem.messages

import java.util.Date

import scala.collection.mutable

class BaseMessage(dateParam : Date = new Date(),
                  metaParam : mutable.HashMap[String,Any] = new mutable.HashMap[String,Any]()) {

  val date = dateParam
  val meta = metaParam

}
