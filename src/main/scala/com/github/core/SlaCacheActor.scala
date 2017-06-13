package com.github.core

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Success
import akka.actor.{Actor, Props}
import akka.pattern._
import com.github.model.commands.SlaCallback
import com.github.model.{SlaCache, Token, User}

class SlaCacheActor extends Actor {

  val clearTimeout: Int = 3 * 1000

  override def preStart(): Unit = {
    super.preStart()
    context.system.scheduler.schedule(3 seconds, 3 seconds, self, ClearCache)
  }

  val slaService = context.actorOf(Props[SlaServiceMock])

  val slaCache = mutable.Map[User, SlaCache]()

  def receive: Receive = {
    case token: Token =>
      val sernderRef = sender()
      slaService ! token
      getCachedData(token).map(slaCallback => sernderRef ! slaCallback)

    case callback: SlaCallback =>
      val user = callback.user
      slaCache.get(user) match {
        case Some(s) =>
          val tokens = s.tokens + callback.token
          val cacheData = SlaCache(System.currentTimeMillis, callback.rps, s.usedRps, tokens)
          slaCache.update(user, cacheData)
        case None =>
          slaCache += callback.user -> SlaCache(System.currentTimeMillis, callback.rps, callback.rps, Set(callback.token))
      }

    case ClearCache =>
      val time: Long = System.currentTimeMillis
      slaCache.foreach { case (k, v) =>
        if (time - v.updatedTime > clearTimeout) slaCache -= k
      }
  }

  def getCachedData(token: Token)(implicit ec: ExecutionContext): Future[SlaCallback] = {
    val x: Future[SlaCallback] = Future(slaCache.find { kv =>
      kv._2.tokens.contains(token)
    }.get).map(all => SlaCallback(all._1, all._2.rps, token))

    val y: Future[SlaCallback] = (slaService ? Token).mapTo[SlaCallback].andThen {
      case Success(calllback) => self ! calllback
    }
    successRace(x, y)
  }

  def successRace[T](f: Future[T], g: Future[T])(implicit ec: ExecutionContext): Future[T] = {
    val p = Promise[T]()
    Seq(f, g).foreach {
      _ foreach p.trySuccess
    }
    p.future
  }
}

case object ClearCache