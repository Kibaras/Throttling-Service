package com.github.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

trait MainRoute {
  val throtlingRoute: Route = {
    get {
      pathSingleSlash {
        optionalHeaderValueByName("Authorisation") { token =>
          complete(token)

          // would be better:
//          case Some(s) => complete("")
//          case None => complete(StatusCodes.Unauthorized)
        }
      }
    }
  }
}
