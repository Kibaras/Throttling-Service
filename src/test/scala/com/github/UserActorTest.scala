package com.github

import akka.actor.Props
import com.github.core.actors.UserActor
import com.github.model.commands.RenewRps

class UserActorTest extends ActorTestTemplate("UserActorSystem") {
  "An user actor" must {
    "answers true if in one request for rps" in {
      val user = system.actorOf(Props(classOf[UserActor], 1))
      user ! self
      expectMsg(true)
    }

    "answers false on the 3th request" in {
      val user = system.actorOf(Props(classOf[UserActor], 1))
      user ! self
      expectMsg(true)
      user ! self
      expectMsg(true)
      user ! self
      expectMsg(false)
    }

    "answers false on the 3th request and must have ability to change rps by new data" in {
      val user = system.actorOf(Props(classOf[UserActor], 1))
      user ! self
      expectMsg(true)
      user ! self
      expectMsg(true)
      user ! self
      expectMsg(false)
      user ! 20
      user ! self
      expectMsg(true)
    }

    "should have ability to renew rps" in {
      val user = system.actorOf(Props(classOf[UserActor], 1))
      user ! self
      expectMsg(true)
      user ! self
      expectMsg(true)
      user ! self
      expectMsg(false)
      user ! RenewRps
      user ! self
      expectMsg(true)
      user ! self
      expectMsg(true)
      user ! self
      expectMsg(false)
    }

  }
}
