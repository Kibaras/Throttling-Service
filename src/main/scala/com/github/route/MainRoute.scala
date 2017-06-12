package com.github.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

trait MainRoute {
  val throtlingRoute: Route = {
    get {
      pathSingleSlash {
        parameter('token.?){ token =>
          complete(token)
        }
//        optionalHeaderValueByName("token") {
//          case Some(s) => complete("")
//          case None => complete(StatusCodes.Unauthorized)}
      }
    }
  }
}
