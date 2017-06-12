package com.github

import com.typesafe.config.ConfigFactory

object Config {
  private val configFile = ConfigFactory.load()
  private val appConfig = configFile.getConfig("app")

  val graceRps = appConfig.getInt("graceRps")
}
