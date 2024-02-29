ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "RentingApp"
  )
libraryDependencies += "org.scalafx" % "scalafx_3" % "20.0.0-R31"
libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.15"
libraryDependencies += "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.15"