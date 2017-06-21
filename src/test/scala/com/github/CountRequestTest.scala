package com.github

import com.github.core.actors.CountRequests
import com.github.model.{Rps, Token, User}
import org.scalatest.{Matchers, WordSpec}

class CountRequestTest extends WordSpec with CountRequests with Matchers {
  "Count Requests" must {
    val user = User("name")

    "method 'getUserByToken' return user by token" in {
      val token = Token.generateToken(6)
      tokenUser += token -> user

      getUserByToken(token) shouldBe Some(User("name"))

      getUserByToken(Token.generateToken(2)) shouldBe None
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
      usedRPS += User("s") -> Rps(10, 1, 2L, false)
      isAllowedForUser(User("s")) shouldBe true
    }
  }
}
