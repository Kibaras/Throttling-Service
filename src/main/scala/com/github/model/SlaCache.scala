package com.github.model

case class SlaCache(updatedTime: Long, rps: Int, tokens: Set[Token]) {
  def newSlaCache(newRps: Int, newToken: Token): SlaCache = SlaCache(System.currentTimeMillis, newRps, tokens + newToken)
}
