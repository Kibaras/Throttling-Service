package com.github.core.actors

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import akka.actor.{Actor, ActorRef}
import com.github.core.SlaService
import com.github.model.commands.RemoveQuerryedToken
import com.github.model.{Sla, Token}
import com.typesafe.scalalogging.LazyLogging

class SlaServiceMock extends Actor with SlaService with LazyLogging {
  import context.dispatcher

  protected val tokenToNameHolder = mutable.Map[String, String]()

  protected val query = mutable.Map[Token, mutable.HashSet[ActorRef]]()

  def receive: Receive = {
    case RemoveQuerryedToken(token) =>
      query -= token

    case token: Token if query.contains(token) =>
      query.get(token).map(_ += sender())
      logger.debug(s"Sla Query $token")

    case token: Token =>
      logger.debug(s"got $token")
      val receiversSet = mutable.HashSet[ActorRef](sender())
      logger.debug(s"Sender inserted to receiversSet $receiversSet")
      query += token -> receiversSet
      val sla = getSlaByToken(token.token)
      query
        .get(token)
        .foreach { requesters =>
          requesters.foreach { receiver =>
            replyWithTimeout(receiver, sla)
              .andThen {
                case _ => self ! RemoveQuerryedToken(token)
              }
          }
        }
  }

  def replyWithTimeout(receiver: ActorRef, msg: Any)(implicit ec: ExecutionContext): Future[Unit] = Future {
    logger.debug(s"Start reply with timeout to $receiver with $msg")
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
