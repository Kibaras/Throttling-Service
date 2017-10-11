package com.github.core.actors

import scala.concurrent.duration._
import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import com.github.Config
import com.github.model.commands.RenewRps
import com.github.model.exceptions.UnauthorizedException

class MainActor extends Actor {
  var emptyRequest: Int = 0
  val graceRps: Int =  Config.graceRps
  val throttlingCounter: ActorRef = context.system.actorOf(Props[UserManagerActor].withDispatcher("custom-dispatcher"))
  implicit val timeout: Timeout = Timeout(1 second)

  def receive: Receive = {
    case Some(s: String) =>
      throttlingCounter forward s

    case None =>
      val senderRef = sender()
      if (graceRps < emptyRequest) {
        emptyRequest += 1
        senderRef ! false
      }
      else senderRef ! UnauthorizedException

    case RenewRps =>
      emptyRequest = 0
  }
}
