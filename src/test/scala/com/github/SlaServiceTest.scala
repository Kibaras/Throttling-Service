package com.github

import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.Random
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.github.core.SlaServiceMock
import com.github.model.{Sla, Token, User}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class SlaServiceTest extends TestKit(ActorSystem("SlaSystem")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  protected val set = mutable.Set[String]()

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "An SlaService" must {
    val sla = system.actorOf(Props[SlaServiceMock])
    "send back Smessages" in {
      sla ! Token(Random.alphanumeric.take(5).mkString)
      expectMsgClass(classOf[Sla])
    }

    "Return one message ~ 250 millis" in {
      sla ! Token(Random.alphanumeric.take(5).mkString)
      expectNoMsg(240 millis)
    }

    "Receive 4 messages less then 1045 millis" in {
      for (a <- 1 to 4) {
        sla ! Token(Random.alphanumeric.take(5).mkString)
      }
      receiveN(4, 1045 millis)
    }

    "Check messages" in {
      val token = Token(Random.alphanumeric.take(5).mkString)
      val set = mutable.Set[String]()

      for (a <- 1 to 20) {
        sla ! token
        expectMsgPF(262 millis) {
          case Sla(user, _) =>
            set += user
        }
      }

      println(set)
      set.size shouldEqual 1
    }
  }
}
