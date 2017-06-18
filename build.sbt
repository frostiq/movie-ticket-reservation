name := "movie-ticket-reservation"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.7",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.7",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.1.0",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
    