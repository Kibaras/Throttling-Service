package com.github.core.actors

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.github.model.commands.{IncreaseReached, IncreaseRps, RenewRps}
import com.github.model.{NewSlaData, Sla, Token, User}
import com.typesafe.scalalogging.LazyLogging

class ThrottlingCounter extends Actor with LazyLogging {
  import context.dispatcher

  val usedRPS = mutable.Map[User, Rps]()
  val tokenUser = mutable.Map[Token, User]()
  val interval: FiniteDuration = 100 millis
  val selfRef: ActorRef = self
  //  val cache: ActorRef = context.actorOf(Props[SlaCacheActor].withDispatcher("custom-dispatcher"))

  val slaService = context.actorOf(Props[SlaServiceMock].withDispatcher("custom-dispatcher"))

  implicit val timeout = Timeout(1000 millis)

  def receive: Receive = {

    case token: Token =>
      val senderRef = sender()
      if (tokenUser.get(token).isDefined)
        senderRef ! isRequestAllowed(token)
      (slaService ? token).mapTo[Sla]
        .map { slaResp =>
          val newData = NewSlaData(slaResp, token)
          selfRef ! newData
          senderRef ! isRequestAllowed(newData)
        }.andThen {
        case Failure(ex) =>
          logger.error(s"token response was failed with ${ex.getMessage}")
          senderRef ! false
      }

    case slaData: NewSlaData =>
      logger.debug(s"renew sla data with $slaData")
      tokenUser += slaData.token -> slaData.user
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

  def isRequestAllowed(token: Token): Boolean = {
    val allowed = tokenUser.get(token).exists(usr =>
      usedRPS.get(usr) match {
        case None => true
        case Some(rps) => rps.used < rps.rps
      })
    if (allowed) increaseRps(token)
    allowed
  }

  def isRequestAllowed(newSla: NewSlaData): Boolean = {
    val allowed = usedRPS.get(newSla.user) match {
      case None => true
      case Some(rps) => rps.used < rps.rps
    }
    if (allowed) increaseRps(newSla.user)
    allowed
  }

  def clearUserData(user: User): Option[Rps] = usedRPS.remove(user)

  def increaseRps(user: User): Unit = selfRef ! IncreaseRps(user)

  def increaseRps(token: Token): Unit = tokenUser.get(token).foreach(user => selfRef ! IncreaseRps(user))

  def requestSlaService(token: Token)(implicit ec: ExecutionContext): Future[Sla] =
    (slaService ? token).mapTo[Sla]

  override def preStart(): Unit = {
    super.preStart()
    context.system.scheduler.schedule(3 seconds, interval, self, IncreaseReached)
    context.system.scheduler.schedule(3 seconds, 1 second, self, RenewRps)
  }
}

case class Rps(rps: Int, used: Int, lastUpd: Long, increased: Boolean)