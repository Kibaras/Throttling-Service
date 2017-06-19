package com.github

import scala.concurrent.duration._
import akka.actor.Props
import com.github.core.actors.ThrottlingCounter
import com.github.model.Token

class RpsCounterTest extends ActorTestTemplate("RpsCounterAS") {

  "Rps counter" must {

    val token2 = Token.generateToken(5)

    "response true on second request in short time" in {
      val rpsActor = system.actorOf(Props[ThrottlingCounter])
      Thread.sleep(40)
      val token = Token.generateToken(4)
      rpsActor ! token
      expectMsg(false)
      Thread.sleep(50)
      rpsActor ! token
      expectMsg(4 millis, true)
    }
  }
}
