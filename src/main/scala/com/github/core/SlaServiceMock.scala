package com.github.core

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import akka.actor.{Actor, ActorRef}
import com.github.model.commands.{RemoveToken, SlaCallback}
import com.github.model.{Sla, Token, User}

class SlaServiceMock extends Actor with SlaService {
  import context.dispatcher

  //  val query = new ConcurrentSkipListMap[Token, KeySetView[ActorRef, lang.Boolean]]()

  val query = mutable.Map[Token, mutable.HashSet[ActorRef]]()

  def receive: Receive = {
    case RemoveToken(token) =>
      query -= token

    case token: Token =>
      val senderRef = sender()
      if (query.contains(token)) {
        query.get(token).map(_ += senderRef)
      } else {
        val receiversSet = mutable.HashSet[ActorRef]()
        receiversSet += senderRef
        query += token -> receiversSet
        getSlaByToken(token.token)
          .map { sla =>
            query.get(token).foreach(_.foreach { receiver =>
              receiver ! SlaCallback(User(sla.user), sla.rps)
            })
          }
          .andThen {
            case _ => self ! RemoveToken(token)
          }
      }

    case _ =>

    case UserToken(user, token) =>
      tokenToNameHolder += token -> user
  }

  override def getSlaByToken(token: String)(implicit ec: ExecutionContext): Future[Sla] = Future {
    Thread.sleep(241 + Random.nextInt(20))
    Sla(getUser(token), Random.nextInt(50) + 1)
  }

  val tokenToNameHolder: mutable.Map[String, String] = mutable.Map[String, String]()

  def getUser(token: String): String = {
    tokenToNameHolder.get(token) match {
      case Some(s) => s
      case None =>
        val user = SlaServiceMock.users(Random.nextInt(SlaServiceMock.totalUsers))
        self ! UserToken(user, token)
        user
    }
  }

  case class UserToken(user: String, token: String)
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

  val totalUsers: Int = SlaServiceMock.users.length
}
