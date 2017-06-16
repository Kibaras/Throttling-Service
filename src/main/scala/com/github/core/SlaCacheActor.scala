package com.github.core

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.github.model.{Sla, SlaCache, Token, User}
import com.typesafe.scalalogging.LazyLogging

class SlaCacheActor extends Actor with LazyLogging {
  import context.dispatcher

  implicit val timeout = Timeout(100 second)

  val clearTimeout: Int = 3 * 1000

  val slaService: ActorRef = context.actorOf(Props[SlaServiceMock], "SlaService")

  override def preStart(): Unit = {
    super.preStart()
    context.system.scheduler.schedule(3 seconds, 3 seconds, self, ClearCache)
  }

  logger.debug(s"Cache actor started")

  val slaCache: mutable.Map[User, SlaCache] = mutable.Map[User, SlaCache]()

  def receive: Receive = {
    case token: Token =>
      val senderRef = sender()
      logger.debug(s"Message $token from $senderRef was gotten")
      requestSlaService(token)
        .map { sla =>
          val newSla = SlaUpd(token, sla)
          self ! newSla
          senderRef ! sla
        }
      getCachedData(token, slaCache)
        .foreach { slaCallback =>
          logger.debug(s"response to $senderRef with $slaCallback")
          senderRef ! slaCallback
        }

    case sla: SlaUpd =>
      logger.debug(s"Starting update cache with $sla")
      if (slaCache.get(sla.user).isDefined) {
        slaCache.get(sla.user)
          .map(cache => cache.newSlaCache(sla.rps, sla.token))
          .foreach(newCache => slaCache += sla.user -> newCache) // update cache
      } else slaCache += sla.user -> SlaCache(getTime, sla.rps, Set(sla.token))

    case ClearCache =>
      val time: Long = System.currentTimeMillis
      slaCache.foreach { case (k, v) =>
        if (time - v.updatedTime > clearTimeout) slaCache -= k
      }
  }

  def getCachedData(token: Token, slaCache: mutable.Map[User, SlaCache]): Option[Sla] = slaCache
    .find { case (_, sla) => sla.tokens.contains(token) }
    .map { case (user, slaData) => Sla(user.name, slaData.rps) }

  def requestSlaService(token: Token)(implicit ec: ExecutionContext): Future[Sla] =
    (slaService ? token).mapTo[Sla]

  def successRace[T](f: Future[T], g: Future[T])(implicit ec: ExecutionContext): Future[T] = {
    val p = Promise[T]()
    Seq(f, g).foreach {
      _ foreach p.trySuccess
    }
    p.future
  }

  def getTime: Long = System.currentTimeMillis

  case class SlaUpd(token: Token, sla: Sla) {
    val user: User = User(sla.user)
    val rps: Int = sla.rps
  }
}

case object ClearCache