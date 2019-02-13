package com.apifortress.afthem.messages

class HttpWrapper() {

  var url : String = null
  var payload : Array[Byte] = null
  var status : Int = 200
  var method : String = null
  var headers : List[(String Tuple2 String)] = null
  var remoteIP: String = null

}
