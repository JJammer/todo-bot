name := "todo"

version := "0.1"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq( 
    "com.typesafe.akka" %% "akka-http" % "10.1.9",
    "com.typesafe.akka" %% "akka-stream" % "2.5.24",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.9",
    "io.spray" %% "spray-json" % "1.3.5",
    "com.typesafe.slick" %% "slick" % "3.3.2",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "org.postgresql" % "postgresql" % "42.2.6",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2",
    "com.bot4s" %% "telegram-core" % "4.4.0-RC1",
    "com.bot4s" %% "telegram-akka" % "4.4.0-RC1",
    "org.scalatest" %% "scalatest" % "3.0.8" % Test,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3",

)