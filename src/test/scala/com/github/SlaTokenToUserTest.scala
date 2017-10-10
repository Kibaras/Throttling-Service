package com.github

import scala.collection.mutable
import scala.util.Random
import akka.actor.Props
import com.github.core.actors.SlaServiceMock
import com.github.model.Sla

class SlaTokenToUserTest extends ActorTestTemplate("SlaTokenToUserSystem") {

  "An SlaService" must {
    val sla = system.actorOf(Props[SlaServiceMock])
    "Must return same user on same token" in {
      val token = Random.alphanumeric.take(5).mkString
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
