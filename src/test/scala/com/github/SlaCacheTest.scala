package com.github

import scala.concurrent.duration._
import akka.actor.Props
import com.github.core.actors.SlaCacheActor
import com.github.model.{Sla, Token}

class SlaCacheTest extends ActorTestTemplate("cache_system") {
  "An SlaCache" must {
    val cache = system.actorOf(Props[SlaCacheActor])
    Thread.sleep(100) // for actors starting
    "return cache less then 280 ms" in {
      val token = Token.generateToken(5)
      cache ! token
      expectMsgClass(280 millis, classOf[Sla])
    }

    "reply faster for second request" in {
      val token = Token.generateToken(6)
      cache ! token
      expectMsgClass(280 millis, classOf[Sla])
      Thread.sleep(280)
      cache ! token
      expectMsgClass(3 millis, classOf[Sla])
    }
  }
}
