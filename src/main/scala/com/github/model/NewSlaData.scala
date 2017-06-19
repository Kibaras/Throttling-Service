package com.github.model

case class NewSlaData(user: User, rps: Int)

object NewSlaData {
//  def apply(user: User, rps: Int): NewSlaData = new NewSlaData(user, rps)

  def apply(sla: Sla): NewSlaData = new NewSlaData(User(sla.user), sla.rps)
}