package com.github.core

import scala.concurrent.Future
import com.github.model.Sla

trait SlaService {
  def getSlaByToken(token: String): Future[Sla]
}
