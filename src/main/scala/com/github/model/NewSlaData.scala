package com.github.model

case class NewSlaData(user: User, rps: Int, token: Token)

object NewSlaData {
//  def apply(user: User, rps: Int): NewSlaData = new NewSlaData(user, rps)

  def apply(sla: Sla, token: Token): NewSlaData = new NewSlaData(User(sla.user), sla.rps, token)
}