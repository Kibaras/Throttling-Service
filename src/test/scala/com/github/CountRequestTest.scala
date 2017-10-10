package com.github

import scala.util.Random
import com.github.core.actors.CountRequests
import com.github.model.{Rps, User}
import org.scalatest.{Matchers, WordSpec}

class CountRequestTest extends WordSpec with CountRequests with Matchers {
  "Count Requests" must {
    val user = "name"

    "method 'getUserByToken' return user by token" in {
      val token = Random.alphanumeric.take(6).mkString
      tokenUser += token -> user

      getUserByToken(token) shouldBe Some(User("name"))

      getUserByToken(Random.alphanumeric.take(6).mkString) shouldBe None
    }

    "method 'increase' must increase 'usedRps' parameter" in {
      val preIncrease = 4
      usedRPS += user -> Rps(100, preIncrease, 2L, false)

      increase(user)

      usedRPS(user).used shouldBe preIncrease + 1
    }

    "method 'isAllowedForUser' must return false if not allowed" in {
      usedRPS += user -> Rps(10, 10, 2L, false)
      isAllowedForUser(user) shouldBe false
    }

    "method 'isAllowedForUser' must return true if allowed" in {
      usedRPS += "s" -> Rps(10, 1, 2L, false)
      isAllowedForUser("s") shouldBe true
    }
  }
}
