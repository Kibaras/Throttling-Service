package com.github.core.actors

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Success
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.github.model.Sla
import com.github.model.commands.RenewRps

class UserManagerActor extends Actor {

  import context.dispatcher

  implicit val timeout = Timeout(1 second)

  val users = new ConcurrentHashMap[String, ActorRef]().asScala
  val cacheTokenToUser = new ConcurrentHashMap[String, String]().asScala

  val slaService: ActorRef = context.actorOf(Props[SlaServiceMock])

  def receive: Receive = {
    case token: String =>
      val senderRef = sender()
      Future {
        getUserFromCache(token)
          .flatMap(getUserActor)
      }.flatMap {
        case Some(aRef) => Future(aRef ! senderRef)
        case None => createUser(token, senderRef)
      }.andThen {
        case _ => getSla(token).foreach(sla => users.get(sla.user).foreach(userRef => userRef ! sla.rps))
      }

    case RenewRps =>
      Future {
        context.parent ! RenewRps
        users.values.foreach(user => user ! RenewRps)
      }
  }

  def getUserFromCache(token: String): Option[String] = cacheTokenToUser.get(token)

  def getUserActor(user: String): Option[ActorRef] = users.get(user)

  def createUser(token: String, senderRef: ActorRef): Future[Unit] = {
    getSla(token).map {
      sla =>
        cacheTokenToUser.putIfAbsent(token, sla.user)
        users.putIfAbsent(sla.user, createUserActor(sla.user, sla.rps))
        sla
    }.andThen {
      case Success(s) =>
        users.get(s.user).foreach(uRef => uRef ! senderRef)
    }.map(_ => {})
  }

  def createUserActor(user: String, rps: Int): ActorRef = context.actorOf(Props(classOf[UserActor], rps), user)

  def getSla(token: String): Future[Sla] = (slaService ? token).mapTo[Sla]

  def updateUserData(user: String, newRps: Int): Unit = {
    users.get(user).foreach(userRef => userRef ! newRps)
  }

  override def preStart(): Unit = {
    super.preStart()
    context.system.scheduler.schedule(3 seconds, 1 second, self, RenewRps)
  }
}
