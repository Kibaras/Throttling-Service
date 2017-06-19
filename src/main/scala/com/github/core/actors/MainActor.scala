package com.github.core.actors

import scala.concurrent.duration._
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern._
import akka.util.Timeout
import com.github.Config
import com.github.model.Token
import com.github.model.exceptions.UnauthorizedException

class MainActor extends Actor {
  import context.dispatcher

  var emptyRequest: Int = 0
  val graceRps: Int =  1// Config.graceRps
  val throttlingCounter: ActorRef = context.system.actorOf(Props[ThrottlingCounter])
  implicit val timeout = Timeout(1 second)

  def receive: Receive = {
    case Some(s: String) =>
      val senderRef = sender()
      (throttlingCounter ? Token(s)) pipeTo senderRef

    case None =>
      val senderRef = sender()
      if (graceRps < emptyRequest) {
        emptyRequest += 1
        senderRef ! false
      }
      else senderRef ! UnauthorizedException
  }
}
