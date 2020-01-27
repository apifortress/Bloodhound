package com.apifortress.afthem.messages.beans

import scala.collection.mutable

object ExpMap{
  def apply: ExpMap = new ExpMap()

  def apply(tuple : Tuple2[String,Any]) : ExpMap = {
    val map = new ExpMap
    map.put(tuple._1,tuple._2)
    return map
  }
}

class ExpMap extends mutable.HashMap[String,Any] {

  def getOrElse(key : String, defaultValue : Any) : Any = super.getOrElse(key,defaultValue)

}
