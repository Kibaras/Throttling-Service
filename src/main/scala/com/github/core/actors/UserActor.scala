package com.github.core.actors

import akka.actor.{Actor, ActorRef}
import com.github.model.commands.RenewRps

class UserActor(initRps: Int) extends Actor {

  @volatile
  var rps = initRps
  @volatile
  var used = 0
  @volatile
  var increased = false

  def receive: Receive = {
    case receiverActor: ActorRef =>
      used += 1
      if (!increased && used > rps) {
        increased = true
        used /= 2
      }
      receiverActor ! (used <= rps)

    case newRps: Int =>
      rps = newRps

    case RenewRps =>
      used = 0
      increased = false
  }
}
