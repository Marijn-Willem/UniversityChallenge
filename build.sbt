scalaVersion := "3.8.4"

lazy val root = rootProject
  .settings(
    name := "UniversityChallenge",
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "skunk-core" % "1.0.0"
    )
  )
