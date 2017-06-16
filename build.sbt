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
  val latest = "latest.release"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % latest,
    "com.typesafe.akka" %% "akka-testkit" % latest % Test,
    "com.typesafe.akka" %% "akka-http" % latest,
    "com.typesafe.akka" %% "akka-http-testkit" % latest % Test,
    "org.scalatest"     %% "scalatest" % latest % Test,
    "com.typesafe.scala-logging"  %% "scala-logging" % latest,
    "ch.qos.logback"    % "logback-classic" % latest
  )
}