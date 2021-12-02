val scala3Version = "3.1.0"

lazy val zioDeps = libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % Versions.zio,
  "dev.zio" %% "zio-test" % Versions.zio,
  "dev.zio" %% "zio-test-sbt" % Versions.zio,
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "zio-playground",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    zioDeps
  )
