normalizedName := "Throttling_Service"

organization := "com.github"

scalaVersion := "2.11.8"

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-encoding",
  "utf8",
  "-feature",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-language:postfixOps"
)

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-actor" % "latest.release",
    "com.typesafe.akka" %% "akka-testkit" % "latest.release" % Test,
    "com.typesafe.akka" %% "akka-http" % "latest.release",
    "com.typesafe.akka" %% "akka-http-testkit" % "latest.release" % Test,
    "org.scalatest" %% "scalatest" % "latest.release" % Test,
    "ch.qos.logback" % "logback-classic" % "latest.release"
  )
}