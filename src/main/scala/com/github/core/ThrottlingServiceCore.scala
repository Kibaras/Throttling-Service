package com.github.core

import scala.concurrent.{ExecutionContext, Future}
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout

class ThrottlingServiceCore(counter: ActorRef, timeout: Timeout) extends ThrottlingService {
  val slaService = counter
  implicit val timeout1 = timeout

  def isRequestAllowed(token: Option[String])(implicit ec: ExecutionContext): Future[Boolean] =
    (slaService ? token).mapTo[Boolean]
}
