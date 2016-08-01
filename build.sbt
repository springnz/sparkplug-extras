// refer to project/Common.scala for shared settings and definitions
// refer to project/Dependencies.scala for dependency definitions
import Common._
import Dependencies._
import Release._
import sbt.Keys._
import xerial.sbt.Pack._

name := "sparkplug-extras"

organization := organisationString
scalaVersion := scalaVersionString

releaseVersionBump := sbtrelease.Version.Bump.Bugfix
releaseProcess := customReleaseProcess

// run the tests in series
concurrentRestrictions in Global += Tags.limit(Tags.Test, 1)

def dep(project: Project) = project % "test->test;compile->compile"

lazy val sparkPlugExtras = CreateProject("sparkplug-extras", sparkExtraLibDependencies)
  .settings(fork := true) // required for OrientDB tests

lazy val sparkPlugExamples = CreateProject("sparkplug-examples", sparkExampleLibDependencies)
  .dependsOn(dep(sparkPlugExtras))

lazy val main = project.in(file("."))
  .aggregate(sparkPlugExtras, sparkPlugExamples)
  .settings(Defaults.coreDefaultSettings ++ Seq(
    publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo"))),
    publishArtifact := false
  ))
  .settings(packAutoSettings)
  .settings(packGenerateWindowsBatFile := false)
  .settings(parallelExecution in Test := false)


