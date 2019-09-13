package com.apifortress.afthem.actors.sidecars

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.apifortress.afthem.TestData
import com.apifortress.afthem.exceptions.{RejectedRequestException, UnauthorizedException}
import com.apifortress.afthem.messages.{ExceptionMessage, WebParsedRequestMessage, WebParsedResponseMessage}
import org.junit.Test

class AccessLoggerActorTests {

  @Test
  def testActor() : Unit = {
    implicit val system = ActorSystem()
    val probe = TestProbe()
    val start = System.currentTimeMillis()
    val actor = system.actorOf(Props(new AccessLoggerActor("abc")))
    val requestMessage = new WebParsedRequestMessage(TestData.createWrapper(),null,null,null)
    val responseMessage = new WebParsedResponseMessage(TestData.createWrapper(), TestData.createWrapper(),null,null,null)
    actor ! requestMessage
    actor ! responseMessage
    actor ! new ExceptionMessage(new RejectedRequestException(requestMessage),500,requestMessage)
    actor ! new ExceptionMessage(new UnauthorizedException(requestMessage),500,requestMessage)
    Thread.sleep(500)
    system.terminate()
  }
}
