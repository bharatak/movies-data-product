name := "marquez"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq("com.softwaremill.sttp.client" %% "core" % "2.0.0-RC1")

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.4.4",
  "org.apache.spark" %% "spark-sql" % "2.4.4"
)

// Note: this library got a vulnerability ... should we go ahead with this?
//libraryDependencies ++= Seq("io.github.marquezproject" % "marquez-java" % "0.2.0-rc.10")
