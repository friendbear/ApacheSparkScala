ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version:= "0.1.0-SNAPSHOT"


resolvers += "Atilika Open Source repository" at "http://www.atilika.org/nexus/content/repositories/atilika"
//libraryDependencies += "org.atilika.kuromoji" % "kuromoji" % "0.7.7"
lazy val commonSettings = Seq(
  version := "0.1.0"
)

unmanagedBase := baseDirectory.value / "libs"

lazy val core = (project in file("core")).settings(
  scalaVersion := "2.12.8",
  name := "spark-core",
  commonSettings,
  libraryDependencies ++= Seq(
    "org.apache.spark" % "spark-core_2.12" % "2.4.3",
    "org.apache.spark" % "spark-sql_2.12" % "2.4.3",
    "org.apache.spark" % "spark-hive_2.12" % "2.4.3",
    "org.scalatest" %% "scalatest" % "3.0.8",
  )
)

val sparkVersion ="2.4.3"
lazy val sub = (project in file("sub")).settings(
  scalaVersion := "2.11.11",

    name := "hi-spark",
    commonSettings,
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion,
      "org.apache.spark" %% "spark-streaming" % sparkVersion,
      "org.apache.bahir" % "spark-streaming-twitter_2.11" % "2.3.3",
      "org.apache.spark" %% "spark-streaming-kafka" % "1.6.3" excludeAll(
        ExclusionRule(organization = "org.spark-project.spark", name = "unused"),
        ExclusionRule(organization = "org.apache.spark", name = "spark-streaming"),
        ExclusionRule(organization = "org.apache.hadoop")
      ),
      //"org.scalatest" %% "scalatest" % "3.0.8", //version changed as these the only versions supported by 2.12
    )
  )
