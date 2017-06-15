package com.github.core

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import akka.actor.{Actor, ActorRef}
import com.github.model.commands.RemoveToken
import com.github.model.{Sla, Token}

class SlaServiceMock extends Actor with SlaService {
  import context.dispatcher

  protected val tokenToNameHolder: mutable.Map[String, String] = mutable.Map[String, String]()

  protected val query = mutable.Map[Token, mutable.HashSet[ActorRef]]()

  def receive: Receive = {
    case RemoveToken(token) =>
      query -= token

    case token: Token if query.contains(token) =>
      query.get(token).map(_ += sender())

    case token: Token =>
      val receiversSet = mutable.HashSet[ActorRef](sender())
      query += token -> receiversSet
      val sla = getSlaByToken(token.token)
      query
        .get(token)
        .map(requesters => requesters.foreach(receiver => replyWithTimeout(receiver, sla)))
        .foreach(_ => self ! RemoveToken(token))
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
        val user = Random.alphanumeric.take(5).mkString
        tokenToNameHolder += token -> user
        user
    }
  }

  case class UserToken(user: String, token: String)
}
