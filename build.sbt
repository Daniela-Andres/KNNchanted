import sbt.Keys.libraryDependencies

name := "myproject"

ThisBuild / version := "0.2"
ThisBuild / scalaVersion := "2.13.2"

lazy val myproject = (project in file("source/application/uniproject"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "myproject",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0",
    scalaJSUseMainModuleInitializer := true,
    Compile / mainClass := Some("de.MainApp")
  )
