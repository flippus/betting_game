name := "betting_game"

version := "1.0-SNAPSHOT"

Keys.fork in Test := false

parallelExecution in Test := false

lazy val root = (project in file(".")).enablePlugins(play.PlayJava)

libraryDependencies ++= Seq(
  javaCore, javaJdbc, javaEbean, javaJpa, cache,
  "com.typesafe" %% "play-plugins-mailer" % "2.1-RC2",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
)
