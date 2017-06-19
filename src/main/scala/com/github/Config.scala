package com.github

import com.typesafe.config.ConfigFactory

object Config {
  private val configFile = ConfigFactory.load()
  private val appConfig = configFile.getConfig("app")

  val bindInterface = appConfig.getString("bindInterface")
  val bindPort = appConfig.getInt("bindPort")

  val graceRps = appConfig.getInt("graceRps")
}
