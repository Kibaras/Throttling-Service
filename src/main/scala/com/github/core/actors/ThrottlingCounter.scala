package com.github.core.actors

import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.Failure
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.github.core.SlaCacheActor
import com.github.model.{NewSlaData, Sla, Token, User}
import com.typesafe.scalalogging.LazyLogging

class ThrottlingCounter extends Actor with LazyLogging {
  import context.dispatcher

  val usedRPS = mutable.Map[User, Rps]()
  val interval: FiniteDuration = 100 millis
  val selfRef: ActorRef = self
  val cache: ActorRef = context.actorOf(Props[SlaCacheActor])

  implicit val timeout = Timeout(250 millis)

  def receive: Receive = {

    case token: Token =>
      val senderRef = sender()
      (cache ? token).mapTo[Sla]
        .map { slaResp =>
          val newData = NewSlaData(slaResp)
          selfRef ! newData
          senderRef ! isRequestAllowed(newData)
        }.andThen {
        case Failure(ex) =>
          logger.error(s"token response was failed with ${ex.getMessage}")
          senderRef ! false
      }

    case slaData: NewSlaData =>
      logger.debug(s"renew sla data with $slaData")
      usedRPS.get(slaData.user) match {
        case None => usedRPS += slaData.user -> Rps(slaData.rps, 0, System.currentTimeMillis, false)
        case Some(s) => usedRPS += slaData.user -> s.copy(rps = slaData.rps)
      }

    case IncreaseReached =>
      logger.debug("Process to check throttling data started")
      val currentTime = System.currentTimeMillis
      usedRPS.foreach { case (user, rpsCounter) =>
        if (currentTime - rpsCounter.lastUpd > 3 * 1000) clearUserData(user)
        else if (rpsCounter.rps - rpsCounter.used <= 0)
          usedRPS += user -> rpsCounter.copy(used = rpsCounter.used - rpsCounter.rps / 10, increased = true)
      }

    case RenewRps =>
      logger.debug("Process to Renew Rps every second started")
      usedRPS.foreach { case (user, rpsCounter) =>
        usedRPS += user -> rpsCounter.copy(used = 0, increased = false)
      }

    case IncreaseRps(user) =>
      usedRPS.get(user).map { rps =>
        usedRPS += user -> rps.copy(used = rps.used + 1)
      }
  }

  def isRequestAllowed(newSla: NewSlaData): Boolean = {
    usedRPS.get(newSla.user) match {
      case None => true
      case Some(rps) => rps.used < rps.rps
    }
  }

  def clearUserData(user: User) = usedRPS.remove(user)

  def increaseRps(user: User) = selfRef ! IncreaseRps(user)

  override def preStart(): Unit = {
    super.preStart()
    context.system.scheduler.schedule(3 seconds, interval, self, IncreaseReached)
    context.system.scheduler.schedule(3 seconds, 1 second, self, RenewRps)
  }
}

case object RenewRps
case object IncreaseReached

case class Rps(rps: Int, used: Int, lastUpd: Long, increased: Boolean)
case class IncreaseRps(user: User)

