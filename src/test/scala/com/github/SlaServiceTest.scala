package com.github

import scala.concurrent.duration._
import scala.util.Random
import akka.actor.Props
import com.github.core.actors.SlaServiceMock
import com.github.model.Sla

class SlaServiceTest extends ActorTestTemplate("SlaSystem"){
  "An SlaService" must {
    "send back messages" in {
      val sla = system.actorOf(Props[SlaServiceMock])
      sla ! Random.alphanumeric.take(2).mkString
      expectMsgClass(classOf[Sla])
    }

    "Return one message ~ 250 millis" in {
      val sla = system.actorOf(Props[SlaServiceMock])
      sla ! Random.alphanumeric.take(3).mkString
      expectNoMsg(240 millis)
    }

    "Receive 4 messages less then 1045 millis" in {
      val sla = system.actorOf(Props[SlaServiceMock])
      for (_ <- 1 to 4) {
        sla ! Random.alphanumeric.take(4).mkString
      }
      receiveN(4, 1045 millis)
    }
  }
}
