package com.github

import scala.concurrent.duration._
import scala.util.Random
import akka.actor.Props
import com.github.core.actors.ThrottlingCounter

class RpsCounterTest extends ActorTestTemplate("RpsCounterAS") {

  "Rps counter" must {

    val token2 = Random.alphanumeric.take(5).mkString

    "response true on second request in short time" in {
      val rpsActor = system.actorOf(Props[ThrottlingCounter])
      Thread.sleep(40)
      val token = Random.alphanumeric.take(4).mkString
      rpsActor ! token
      expectMsg(false)
      Thread.sleep(100)
      rpsActor ! token
      expectMsg(5 millis, true)
    }
  }
}
