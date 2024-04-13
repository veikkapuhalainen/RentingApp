ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "RentingApp"
  )
libraryDependencies += "org.scalafx" % "scalafx_3" % "20.0.0-R31"
libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.15"
libraryDependencies += "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.15"
libraryDependencies ++= Seq(
  "org.openjfx" % "javafx-controls" % "19.0.2",
  "org.openjfx" % "javafx-fxml" % "19.0.2",
  "org.openjfx" % "javafx-web" % "19.0.2",
  "org.openjfx" % "javafx-media" % "19.0.2",
  "org.openjfx" % "javafx-swing" % "19.0.2",
  "org.openjfx" % "javafx-base" % "19.0.2"
)
libraryDependencies += "com.calendarfx" % "view" % "11.12.7"


libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.14.3",
  "io.circe" %% "circe-generic" % "0.14.3",
  "io.circe" %% "circe-parser" % "0.14.3"
)