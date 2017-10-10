package com.github.core.actors

import java.util.concurrent.ConcurrentHashMap
import scala.collection.convert.decorateAsScala._
import com.github.model.{Rps, User}

trait CountRequests {

  val usedRPS = new ConcurrentHashMap[String, Rps]().asScala
  val tokenUser = new ConcurrentHashMap[String, String]().asScala

  def getUserByToken(token: String): Option[String] = tokenUser.get(token)

  def clearUserData(user: String): Option[Rps] = usedRPS.remove(user)

  def isAllowedForUser(user: String): Boolean = usedRPS.get(user) match {
    case None => false
    case Some(rps) => rps.used < rps.rps
  }

  def increase(user: String) = usedRPS
    .get(user)
    .foreach(rps => usedRPS += user -> rps.copy(used = rps.used + 1))
}
