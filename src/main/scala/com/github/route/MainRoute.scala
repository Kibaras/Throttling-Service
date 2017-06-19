package com.github.route

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.core.ThrottlingService

trait MainRoute {
  val throttlingService: ThrottlingService
  implicit val dispatcher: ExecutionContext

  val throtlingRoute: Route = {
    get {
      pathSingleSlash {
        optionalHeaderValueByName("Authorization") { token =>
          onComplete(throttlingService.isRequestAllowed(token)) {
            case Success(s) => complete(s.toString)
            case Failure(ex) => complete(StatusCodes.Unauthorized)
          }
        }
      }
    }
  }
}
