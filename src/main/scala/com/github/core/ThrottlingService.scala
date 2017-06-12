package com.github.core

import scala.concurrent.{ExecutionContext, Future}

trait ThrottlingService {
  val graceRps: Int // configurable
  val slaService: SlaService // use mocks/stubs for testing
  // Should return true if the request is within allowed RPS.
  def isRequestAllowed(token: Option[String])(implicit ec: ExecutionContext): Future[Boolean]
}