package com.github

import scala.concurrent.duration._
import scala.util.Random
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.github.core.SlaServiceMock
import com.github.model.{Sla, Token}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class SlaServiceTest extends ActorTestTemplate("SlaSystem"){

  "An SlaService" must {
    "send back Smessages" in {
      val sla = system.actorOf(Props[SlaServiceMock])
      sla ! Token(Random.alphanumeric.take(2).mkString)
      expectMsgClass(classOf[Sla])
    }

    "Return one message ~ 250 millis" in {
      val sla = system.actorOf(Props[SlaServiceMock])
      sla ! Token(Random.alphanumeric.take(3).mkString)
      expectNoMsg(240 millis)
    }

    "Receive 4 messages less then 1045 millis" in {
      val sla = system.actorOf(Props[SlaServiceMock])
      for (a <- 1 to 4) {
        sla ! Token(Random.alphanumeric.take(4).mkString)
      }
      receiveN(4, 1045 millis)
    }
  }
}
