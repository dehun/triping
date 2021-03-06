name := "tripingMaster"

version := "1.0-SNAPSHOT"

mainClass := Some("MasterMain")

scalaVersion := "2.11.7"

libraryDependencies ++=
  Seq("com.typesafe.akka" %% "akka-actor" % "2.4.17",
    "com.typesafe.akka" %% "akka-remote" % "2.4.17",
    "com.typesafe.akka" %% "akka-http-core" % "10.0.4",
    "com.typesafe.akka" %% "akka-http" % "10.0.4",
    "org.typelevel" %% "cats" % "0.9.0",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )

import NativePackagerHelper._

enablePlugins(JavaServerAppPackaging)

mappings in Universal += {
  // we are using the reference.conf as default application.conf
  // the user can override settings here
  val conf = (resourceDirectory in Compile).value / "application.conf"
  conf -> "application.conf"
}

