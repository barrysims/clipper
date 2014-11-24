import AssemblyKeys._

 name := "clipper"

organization := "barrysims"

description := "Intellij scala clipboard helper"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-target:jvm-1.6")

libraryDependencies ++= Seq(
  "com.danieltrinh" %% "scalariform" % "0.1.5",
  "org.scalatest" % "scalatest_2.10" % "2.2.0" % "test")

unmanagedBase := baseDirectory.value / "none"

assemblySettings

outputPath in assembly := file("./lib/clipper-scala.jar")
