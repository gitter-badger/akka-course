name := "akka-course"

version := "1.0"

scalaVersion := "2.11.8"

fork := true

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Xlint"
//  "-Xfatal-warnings" - disabled as ??? produces 'dead code after this construct'
)

testOptions in Test += Tests.Argument("-oW")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.7",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.7",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.7",
  "ch.qos.logback"      % "logback-classic" % "1.1.2",
  "org.scalatest"      %% "scalatest"       % "2.2.6" % "test")

