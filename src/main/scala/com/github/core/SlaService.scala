package com.github.core

import com.github.model.Sla

trait SlaService {
  def getSlaByToken(token: String): Sla
}
