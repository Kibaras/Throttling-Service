package com.github.core.actors

import java.util.concurrent.ConcurrentHashMap
import scala.collection.convert.decorateAsScala._
import akka.actor.Actor

class CacheActor extends Actor {
  val cacheTokenToUser = new ConcurrentHashMap[String, String]().asScala

  def receive: Receive = {
    case token: String =>

  }
}


