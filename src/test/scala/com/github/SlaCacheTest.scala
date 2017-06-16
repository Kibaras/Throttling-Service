package com.github

import scala.concurrent.duration._
import akka.actor.Props
import com.github.core.SlaCacheActor
import com.github.model.{Sla, Token}

class SlaCacheTest extends ActorTestTemplate("cache_system") {
  "An SlaCache" must {
    "return cache less then 270 ms" in {
      val cache = system.actorOf(Props[SlaCacheActor])
      Thread.sleep(100)
      val token = Token.generateToken(5)
      cache ! token
      expectMsgClass(270 millis, classOf[Sla])
    }
  }
}
