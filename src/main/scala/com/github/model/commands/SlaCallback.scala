package com.github.model.commands

import com.github.model.{Token, User}

case class SlaCallback(
  user: User,
  rps: Int,
  token: Token
)