package com.github

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.github.core.actors.MainActor
import com.github.core.{ThrottlingService, ThrottlingServiceCore}
import com.github.route.MainRoute
import com.typesafe.scalalogging.LazyLogging

object Boot extends App with MainRoute with LazyLogging {
  implicit val actorSystem = ActorSystem("ThrottlingSystem")
  implicit val dispatcher: ExecutionContext = actorSystem.dispatcher
  implicit val timeout = Timeout(1 second)
  implicit val materializer = ActorMaterializer()

  val mainActor = actorSystem.actorOf(Props[MainActor])

  val throttlingService: ThrottlingService = new ThrottlingServiceCore(mainActor, timeout)

  Http().bindAndHandle(throtlingRoute, Config.bindInterface, Config.bindPort).andThen{
    case Success(s) => logger.info(s"service started at ${Config.bindInterface} port:${Config.bindPort}")
    case Failure(ex) => logger.error(s"service failed with ${ex.getMessage}")
  }
}
