package com.apifortress.afthem.actors.filters

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestProbe}
import com.apifortress.afthem.TestData
import com.apifortress.afthem.config.Phase
import com.apifortress.afthem.exceptions.AfthemFlowException
import com.apifortress.afthem.messages.beans.AfthemResult
import com.apifortress.afthem.messages.{BaseMessage, WebParsedRequestMessage}
import org.junit.Assert._
import org.junit.Test

import scala.concurrent.duration._

class DelayActorTests {


  @Test
  def testActor() : Unit = {
    val result = new AfthemResult(){
      override def setData(exception: Exception, status: Int, contentType: String, message: BaseMessage): Unit = {
        assertEquals(401,status)
      }
    }

    implicit val system = ActorSystem()
    val probe = TestProbe()
    val start = System.currentTimeMillis()
    val actor = system.actorOf(Props(new DelayActor("abc") {
      override def tellNextActor(message: BaseMessage) : Unit = {
        probe.ref ! message
      }
      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc","next",List.empty[String],Map("delay"->"2 seconds"))
      }
    }))
    val message = new WebParsedRequestMessage(TestData.createWrapper(),null,null,result)
    actor ! message
    val request = probe.expectMsgClass(5 seconds, classOf[WebParsedRequestMessage])
    assertTrue(System.currentTimeMillis()>start+1500)
    Thread.sleep(500)
    system.terminate()
  }

  @Test(expected = classOf[AfthemFlowException])
  def testActorExeception() : Unit = {
    implicit val system = ActorSystem()
    val start = System.currentTimeMillis()
    val actor = TestActorRef(new DelayActor("abc") {
      override def getPhase(message: BaseMessage): Phase = {
        new Phase("abc","next",List.empty[String],Map("delay"->"2 parsec"))
      }
    })
    val message = new WebParsedRequestMessage(TestData.createWrapper(),null,null,null)
    actor.receive(message)
    Thread.sleep(500)
    system.terminate()
  }
}
