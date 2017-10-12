import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.util.Random
import scala.concurrent.duration._

class ThrottlingSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://localhost:8080")
		.inferHtmlResources()
		.acceptHeader("*/*")
		.authorizationHeader("adsfadsf")
		.userAgentHeader("curl/7.49.1")



	val scn = scenario("ThrottlingSimulation").exec(Requests.request)


	setUp(scn.inject(atOnceUsers(10))).protocols(httpProtocol)
}

object Requests {
  def header = Map("Authentication" -> Random.alphanumeric.take(7).mkString)

  def request = during(10, "n", false) {
		exec(http("request_${n}")
			.get("/")
			.headers(header)
			.check(bodyString.is("true")))
  }
}
