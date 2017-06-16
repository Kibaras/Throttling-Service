package com.github.model

import scala.util.Random

case class Token(token: String)

object Token{
  def generateToken(length: Int): Token = Token(Random.alphanumeric.take(length).mkString)
}
