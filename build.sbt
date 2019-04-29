/**
  * library dependencies
  */
val vlog4j = "2.9.0"
lazy val log4j_lbs = Seq(
  libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % vlog4j withSources() withJavadoc(),
  libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % vlog4j withSources() withJavadoc(),
  // needed for third party deps...
  libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.25"
)

// scala test
val vscl_tst = "3.0.1"
lazy val tst_lbs = Seq(
  libraryDependencies += "junit" % "junit" % "4.10",
  libraryDependencies += "org.scalactic" %% "scalactic" % vscl_tst,
  //libraryDependencies += "org.scalatest" %% "scalatest" % vscl_tst % Test
  libraryDependencies += "org.scalatest" %% "scalatest" % vscl_tst
)

// scala swing
lazy val swng_lbs = Seq(
  libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "2.0.0"
)

/**
  * common settings
  */
lazy val cmmn = Seq(
  organization := "ik.org",
  version := "0.1.0",
  scalaVersion := "2.12.6",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:reflectiveCalls", "-language:postfixOps", "-language:implicitConversions"),
  javaOptions in run += "-Xmx8G",
  //javaOptions in run += "-Djava.library.path=/workstem/g3/jvm/lib/linux",
  fork := true,
  connectInput in run := true,

) ++ log4j_lbs ++ tst_lbs

/**
  * project definitions
  */
lazy val util = (project in file("util")).settings(cmmn)
lazy val viz = (project in file("viz")).dependsOn(util).settings(cmmn).settings(swng_lbs)

/**
  * convenient project groupings
  */
lazy val all = (project in file(".")).aggregate(util, viz)
