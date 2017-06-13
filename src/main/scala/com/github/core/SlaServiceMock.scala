package com.github.core

import java.lang
import java.util.concurrent.ConcurrentHashMap.KeySetView
import java.util.concurrent.{ConcurrentHashMap, ConcurrentSkipListMap}
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import akka.actor.{Actor, ActorRef}
import com.github.model.commands.{RemoveToken, SlaCallback}
import com.github.model.{Sla, Token, User}

class SlaServiceMock extends Actor with SlaService {
  import context.dispatcher

  val query = new ConcurrentSkipListMap[Token, KeySetView[ActorRef, lang.Boolean]]()

  def receive: Receive = {
    case RemoveToken(token) =>
      query.remove(token)

    case token: Token =>
      val senderRef = sender()
      if (query.containsKey(token)) {
        query.get(token).add(senderRef)
      } else {
        val receiversSet = ConcurrentHashMap.newKeySet[ActorRef]()
        receiversSet.add(senderRef)
        query.put(token, receiversSet)
        getSlaByToken(token.token)
          .map { sla =>
            query.get(token).asScala.foreach { receiver =>
              receiver ! SlaCallback(User(sla.user), sla.rps, token)
            }
          }
          .andThen {
            case _ => self ! RemoveToken(token)
          }
      }
  }

  override def getSlaByToken(token: String)(implicit ec: ExecutionContext): Future[Sla] = Future {
    Thread.sleep(240)
    Sla(SlaServiceMock.getUser, Random.nextInt(10) + 1)
  }
}

object SlaServiceMock {
  val users: Vector[String] = Vector[String](
    "max",
    "roman",
    "exler",
    "victor",
    "cesar",
    "homer",
    "bart",
    "lisa",
    "meggy",
    "marge")

  val totalUsers = users.length

  def getUser: String = users(Random.nextInt(totalUsers + 1))
}
