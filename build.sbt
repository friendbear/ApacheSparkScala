ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version:= "0.1.0-SNAPSHOT"


resolvers += "Atilika Open Source repository" at "http://www.atilika.org/nexus/content/repositories/atilika"
//libraryDependencies += "org.atilika.kuromoji" % "kuromoji" % "0.7.7"
lazy val commonSettings = Seq(
  version := "0.1.0"
)

lazy val root = (project in file(".")).
  settings(
    name := "hi-spark",
    commonSettings,
    crossSbtVersions := Vector("1.0.0"),
    libraryDependencies ++= Seq(
      "org.apache.spark" % "spark-core_2.12" % "2.4.3",
      "org.apache.spark" % "spark-sql_2.12" % "2.4.3",
      "org.apache.spark" % "spark-hive_2.12" % "2.4.3",
      "org.scalatest" %% "scalatest" % "3.0.8", //version changed as these the only versions supported by 2.12
    )
  )

