package com.github.core

import scala.concurrent.{ExecutionContext, Future}
import com.github.model.Sla

trait SlaService {
  def getSlaByToken(token: String)(implicit ec: ExecutionContext): Future[Sla]
}
