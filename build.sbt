normalizedName := "Throttling_Service"

organization := "com.github"

scalaVersion := "2.12.3"

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
  val akkaHttp = "10.0.10"
  val akka = "2.4.19"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akka,
    "com.typesafe.akka" %% "akka-testkit" % akka % Test,
    "com.typesafe.akka" %% "akka-http" % akkaHttp,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttp % Test,
    "org.scalatest"     %% "scalatest" % latest % Test,
    "com.typesafe.scala-logging"  %% "scala-logging" % latest,
    "ch.qos.logback"    % "logback-classic" % latest
  )
}