lazy val commonSettings = Seq(
  name := "tempas-archivespark",
  organization := "de.l3s",
  version := "1.0.1",
  scalaVersion := "2.11.8",
  fork := true
)

lazy val tempas_archivespark = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "2.2.0" % "provided" excludeAll(
        ExclusionRule(organization = "org.apache.httpcomponents", name = "httpclient"),
        ExclusionRule(organization = "org.apache.httpcomponents", name = "httpcore")),
      "org.apache.hadoop" % "hadoop-client" % "2.6.0" % "provided" excludeAll(
        ExclusionRule(organization = "org.apache.httpcomponents", name = "httpclient"),
        ExclusionRule(organization = "org.apache.httpcomponents", name = "httpcore")),
      "com.github.helgeho" %% "archivespark" % "2.7" % "provided"
    ),
    resolvers ++= Seq(
      Resolver.mavenLocal
    )
  )

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}
