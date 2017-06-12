normalizedName := "Throttling_Service"

organization := "com.github"

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
    "com.typesafe.akka" %% "akka-actor" % "2.5.2",
    "com.typesafe.akka" %% "akka-testkit" % "2.5.2" % Test,
    "com.typesafe.akka" %% "akka-http" % "10.0.7",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.0.7" % Test,
    "org.scalatest" %% "scalatest" % "latest.release" % Test,
    "ch.qos.logback" % "logback-classic" % "latest.release"
  )
}