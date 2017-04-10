name := "play-scala"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
lazy val tripingMaster = project.dependsOn(root)
lazy val tripingSlave = project.dependsOn(root)


scalaVersion := "2.11.7"

import NativePackagerHelper._

enablePlugins(JavaServerAppPackaging)

mappings in Universal += {
  // we are using the reference.conf as default application.conf
  // the user can override settings here
  val conf = (resourceDirectory in Compile).value / "application.conf"
  conf -> "application.conf"
}

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-remote" % "2.4.17",
  "org.scalaz" %% "scalaz-core" % "7.2.10"
)

