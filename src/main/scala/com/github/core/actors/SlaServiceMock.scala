package com.github.core.actors

import java.util.concurrent.ConcurrentHashMap
import scala.collection.convert.decorateAsScala._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import akka.actor.{Actor, ActorRef}
import com.github.core.SlaService
import com.github.model.Sla
import com.github.model.commands.RemoveQuerryedToken
import com.typesafe.scalalogging.LazyLogging

class SlaServiceMock extends Actor with SlaService with LazyLogging {
  import context.dispatcher

  private[this] val tokenToNameHolder = new ConcurrentHashMap[String, String]().asScala

  private[this] val query = new ConcurrentHashMap[String, Set[ActorRef]]().asScala

  def receive: Receive = {
    case RemoveQuerryedToken(token) =>
      query -= token

    case token: String if query.contains(token) =>
      query.get(token).foreach { e =>
        query(token) = e + sender()
      }
      logger.debug(s"Sla Query $token")

    case token: String =>
      logger.debug(s"got $token")
      val receiversSet = Set[ActorRef](sender())
      logger.debug(s"Sender inserted to receiversSet $receiversSet")
      query += token -> receiversSet
      val sla = getSlaByToken(token)
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

  def getSlaByToken(token: String): Sla =
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
}
