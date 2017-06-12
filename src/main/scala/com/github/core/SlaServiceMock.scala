package com.github.core

import com.github.model.Sla

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class SlaServiceMock extends SlaService {
  override def getSlaByToken(token: String)(implicit ec: ExecutionContext): Future[Sla] = Future {
    Thread.sleep(240)
    Sla(SlaServiceMock.getUser, Random.nextInt(10) + 1)
  }
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

  val totalUsers = users.length

  def getUser: String = users(Random.nextInt(totalUsers + 1))
}
