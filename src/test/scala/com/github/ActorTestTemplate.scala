package com.github

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

abstract class ActorTestTemplate(systemName: String) extends TestKit(ActorSystem(systemName)) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
}
