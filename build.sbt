scalaVersion := "3.8.4"

assembly / mainClass := Some("http.Main")
assembly / assemblyJarName := "universitychallenge.jar"

lazy val root = (project in file("."))
  .settings(
    name := "UniversityChallenge",
    libraryDependencies ++= Seq(
      "org.postgresql" % "postgresql" % "42.7.13",
      "org.apache.pekko" %% "pekko-actor-typed" % "1.7.0",
      "org.apache.pekko" %% "pekko-stream" % "1.7.0",
      "org.apache.pekko" %% "pekko-http" % "1.4.0"
    )
  )
