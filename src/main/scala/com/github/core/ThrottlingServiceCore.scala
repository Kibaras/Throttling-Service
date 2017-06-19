package com.github.core

import scala.concurrent.{ExecutionContext, Future}
import com.github.Config

object ThrottlingServiceCore extends ThrottlingService {
  val graceRps: Int = Config.graceRps
  val slaService: SlaService = new SlaServiceMock

  def isRequestAllowed(token: Option[String])(implicit ec: ExecutionContext): Future[Boolean] = token match {
    //    case Some(tokenCheck) => slaService.getSlaByToken(tokenCheck).map(_.rps > 0) // ???
    case None => Future(false)
  }
}
