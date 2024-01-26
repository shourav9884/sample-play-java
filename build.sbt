name := """sample-play-java"""
organization := "com.personal"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  guice,
  javaJpa,
  "org.hibernate" % "hibernate-core" % "6.3.1.Final", // replace by your jpa implementation
  "org.playframework" %% "play-slick" % "6.0.0",
//  "org.playframework" %% "play-slick-evolutions" % "6.0.0",
  "org.postgresql" % "postgresql" % "42.5.1",
  "org.apache.poi" % "poi" % "5.0.0",
  "org.apache.poi" % "poi-ooxml" % "5.0.0"
)
