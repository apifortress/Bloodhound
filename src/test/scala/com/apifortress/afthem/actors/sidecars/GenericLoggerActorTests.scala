package com.apifortress.afthem.actors.sidecars

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.apifortress.afthem.TestData
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.exceptions.{RejectedRequestException, UnauthorizedException}
import com.apifortress.afthem.messages.{BaseMessage, ExceptionMessage, WebParsedRequestMessage, WebParsedResponseMessage}
import org.junit.Test

class GenericLoggerActorTests {

  @Test
  def testActor() : Unit = {
    implicit val system = ActorSystem()
    val start = System.currentTimeMillis()
    val actor = system.actorOf(Props(new GenericLoggerActor("abc"){
      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc","next",List.empty[String],Map("value"->"#msg.request()", "evaluated"->true))
      }
    }))
    val requestMessage = new WebParsedRequestMessage(TestData.createWrapper(),null,null,null)
    actor ! requestMessage
    Thread.sleep(500)
    system.terminate()
  }
}
