package com.github.core

import com.github.Config

import scala.concurrent.{ExecutionContext, Future}

object ThrottlingServiceCore extends ThrottlingService {
  val graceRps: Int = Config.graceRps
  val slaService: SlaService = new SlaServiceMock

  def isRequestAllowed(token: Option[String])(implicit ec: ExecutionContext): Future[Boolean] = token match {
    case Some(tokenCheck) => slaService.getSlaByToken(tokenCheck).map(_.rps > 0) // ???
    case None => Future(false)
  }
}
