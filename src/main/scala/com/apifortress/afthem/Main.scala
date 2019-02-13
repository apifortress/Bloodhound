package com.apifortress.afthem

import com.apifortress.afthem.actors.AppContext
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{Bean, ComponentScan}

@SpringBootApplication
@ComponentScan
class Main

object Main {


  def main(args: Array[String]): Unit = {
    AppContext.init()
    SpringApplication.run(classOf[Main])
  }


}