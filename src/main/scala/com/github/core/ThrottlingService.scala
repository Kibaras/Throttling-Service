package com.github.core

import scala.concurrent.{ExecutionContext, Future}
import akka.actor.ActorRef

trait ThrottlingService {

  val slaService: ActorRef // use mocks/stubs for testing
  // Should return true if the request is within allowed RPS.
  def isRequestAllowed(token: Option[String])(implicit ec: ExecutionContext): Future[Boolean]
}