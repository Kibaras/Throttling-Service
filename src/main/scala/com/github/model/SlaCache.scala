package com.github.model

case class SlaCache(
  updatedTime: Long,
  rps: Int,
  usedRps: Int,
  tokens: Set[Token])
