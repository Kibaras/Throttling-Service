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
  val akkattp = "10.0.3"
  val akka = "2.4.17"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akka,
    "com.typesafe.akka" %% "akka-testkit" % akka % Test,
    "com.typesafe.akka" %% "akka-http" % akkattp,
    "com.typesafe.akka" %% "akka-http-testkit" % akkattp % Test,
    "org.scalatest"     %% "scalatest" % latest % Test,
    "com.typesafe.scala-logging"  %% "scala-logging" % latest,
    "ch.qos.logback"    % "logback-classic" % latest
  )
}