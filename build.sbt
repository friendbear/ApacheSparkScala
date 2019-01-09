name := "ApacheSparkScala"

version := "0.1"

scalaVersion := "2.12.8"

resolvers += "Atilika Open Source repository" at "http://www.atilika.org/nexus/content/repositories/atilika"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.4.0"
)
//libraryDependencies += "org.atilika.kuromoji" % "kuromoji" % "0.7.7"

assemblyMergeStrategy in assembly := {
//  case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
//  case m if m.startsWith("META-INF") => MergeStrategy.discard
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".xml" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".types" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
  case "application.conf"                            => MergeStrategy.concat
  case "unwanted.txt"                                => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
//mainClass in assembly := Some("local.m2.RatingsCounter")
assemblyJarName in assembly := "kumasora.jar"
