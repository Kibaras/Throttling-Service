package com.github

import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.Random
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.github.core.SlaServiceMock
import com.github.model.{Sla, Token}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class SlaTokenToUserTest extends TestKit(ActorSystem("SlaTokenToUserSystem")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "An SlaService" must {
    val sla = system.actorOf(Props[SlaServiceMock])
    "Check messages" in {
      val token = Token(Random.alphanumeric.take(5).mkString)
      val set = mutable.Set[String]()

      sla ! token
      expectMsgPF() {
        case Sla(user, _) =>
          set += user
          println(1)
      }

      Thread.sleep(10)

      sla ! token
      expectMsgPF() {
        case Sla(user, _) =>
          set += user
          println(2)
      }

      Thread.sleep(10)

      sla ! token
      expectMsgPF() {
        case Sla(user, _) =>
          set += user
          println(3)
      }

      println(set)
      set.size shouldEqual 1
    }
  }
}
