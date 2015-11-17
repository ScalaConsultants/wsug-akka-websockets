name := "wsug-akka-websockets"

version := "1.0"

scalaVersion := "2.11.7"

organization := "io.scalac"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8", "-language:postfixOps")

libraryDependencies ++= {
  val logbackV = "1.1.3"
  val akkaV = "2.4.0"
  val akkaHttpV = "2.0-M1"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaHttpV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaHttpV % "test",
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "org.scalatest" %% "scalatest" % "2.2.1" % "test"
  )
}
