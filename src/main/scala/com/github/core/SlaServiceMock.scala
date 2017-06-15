package com.github.core

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import akka.actor.{Actor, ActorRef}
import com.github.model.commands.{RemoveToken, SlaCallback}
import com.github.model.{Sla, Token, User}

class SlaServiceMock extends Actor with SlaService {
  import context.dispatcher

  protected val tokenToNameHolder: mutable.Map[String, String] = mutable.Map[String, String]()

  protected val query = mutable.Map[Token, mutable.HashSet[ActorRef]]()

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
        val sla = getSlaByToken(token.token)

        query
          .get(token)
          .map(_.foreach(receiver => replyWithTimeout(receiver, SlaCallback(User(sla.user), sla.rps))))
          .foreach(_ => self ! RemoveToken(token))
      }

    case UserToken(user, token) =>
      tokenToNameHolder += token -> user
  }

  def replyWithTimeout(receiver: ActorRef, msg: Any)(implicit ec: ExecutionContext): Future[Unit] = Future {
    Thread.sleep(241 + Random.nextInt(20))
    receiver ! msg
  }

  override def getSlaByToken(token: String): Sla =
    Sla(getUser(token), Random.nextInt(50) + 1)

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
