/*
 *   Copyright 2019 API Fortress
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   @author Simone Pezzano
 *
 */

package com.apifortress.afthem


object Metric {
  val METRIC_PROCESS_START = "__process_start"
  val METRIC_START = "__start"
  val METRIC_DOWNLOAD_TIME = "__download_time"
}

/**
  * Utility class to calculate the execution time of a procedure
  * @param start time in nanoseconds. Use the argument if you already have a time measurement, otherwise
  *              disregard
  */
class Metric(val start: Long = System.nanoTime()) {

  /**
    * Calculates the difference between the moment the object was created and now, in milliseconds,
    * rounded to the second decimal digit
    * @return the difference between the moment the object was created and now
    */
  def time() : Double = BigDecimal((System.nanoTime()-start)/1000000.0f).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

  override def toString() : String = return time()+"ms"
}
