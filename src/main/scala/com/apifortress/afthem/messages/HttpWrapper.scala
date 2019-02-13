package com.apifortress.afthem.messages

/**
  * Copyright 2019 API Fortress
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  * @author Simone Pezzano
  *
  * An object wrapping information about either a request or a response
  */
class HttpWrapper() {

  var url : String = null
  var payload : Array[Byte] = null
  var status : Int = 200
  var method : String = null
  var headers : List[(String Tuple2 String)] = null
  var remoteIP: String = null

}
