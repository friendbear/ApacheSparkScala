ThisBuild / organization := "com.example"
ThisBuild / version:= "0.1.0-SNAPSHOT"


resolvers += "Atilika Open Source repository" at "http://www.atilika.org/nexus/content/repositories/atilika"
//libraryDependencies += "org.atilika.kuromoji" % "kuromoji" % "0.7.7"
lazy val commonSettings = Seq.empty[sbt.Def.SettingsDefinition]

unmanagedBase := baseDirectory.value / "libs"

lazy val core = (project in file("core")).settings(
  name := "hi-spark",
  scalaVersion := "2.12.8",
  libraryDependencies ++= Seq(
    "org.apache.spark" % "spark-core_2.12" % "2.4.3",
    "org.apache.spark" % "spark-sql_2.12" % "2.4.3",
    "org.apache.spark" % "spark-hive_2.12" % "2.4.3",
    "org.scalatest" %% "scalatest" % "3.0.8"
  )

)

val spark1Version = "1.6.1"
lazy val sub = (project in file("sub")).settings(
  name := "spark-streaming-twitter",
  scalaVersion := "2.11.8",
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-streaming" % spark1Version,
    "org.apache.bahir" %% "spark-streaming-twitter" % "2.1.0",
  )
)
