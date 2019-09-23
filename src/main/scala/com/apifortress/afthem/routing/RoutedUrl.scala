package com.apifortress.afthem.routing

class RoutedUrl(val url: String, val countDown: Int = -1, val countUp: Int = -1) {

  var counterDown: Int = 0
  var counterUp: Int = countUp

  def isUp() : Boolean = if (countUp == -1) true else (counterUp == countUp && counterDown < countDown)

  def isDown() : Boolean = if (countDown == -1) false else (counterUp < countUp && counterDown == countDown)

  def up() : Unit = {
    if(counterUp < countUp)
      counterUp+=1
    if(counterUp == countUp)
      counterDown = 0
  }
  def down() : Unit = {
    if(counterDown > countDown)
      counterDown+=1
    if(counterDown == countDown)
      counterUp = 0
  }

}