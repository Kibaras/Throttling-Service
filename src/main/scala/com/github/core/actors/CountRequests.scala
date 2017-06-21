package com.github.core.actors

import scala.collection.mutable
import com.github.model.{Rps, Token, User}

trait CountRequests {

  val usedRPS = mutable.Map[User, Rps]()
  val tokenUser = mutable.Map[Token, User]()

  def getUserByToken(token: Token): Option[User] = tokenUser.get(token)

  def clearUserData(user: User): Option[Rps] = usedRPS.remove(user)

  def isAllowedForUser(user: User): Boolean = usedRPS.get(user) match {
    case None => false
    case Some(rps) => rps.used < rps.rps
  }

  def increase(user: User) = usedRPS.get(user).map { rps =>
    usedRPS += user -> rps.copy(used = rps.used + 1)
  }
}
