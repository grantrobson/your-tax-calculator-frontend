import TestPhases.oneForkedJvmPerTest
import play.sbt.routes.RoutesKeys._
import uk.gov.hmrc.DefaultBuildSettings.{addTestReportOption, _}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._

name := "your-tax-calculator-frontend"

lazy val root = (project in file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(PlayKeys.playDefaultPort := 9241)

routesImport ++= Seq("uk.gov.hmrc.domain._")
publishingSettings
unmanagedResourceDirectories in Compile += baseDirectory.value / "resources"
defaultSettings()

scalaVersion := "2.11.11"
crossScalaVersions := Seq("2.11.11")

libraryDependencies ++= AppDependencies()
retrieveManaged := true
evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false)
routesGenerator := StaticRoutesGenerator

Keys.fork in IntegrationTest := false
unmanagedSourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest)(base => Seq(base / "it")).value
addTestReportOption(IntegrationTest, "int-test-reports")
testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value)
parallelExecution in IntegrationTest := false

resolvers ++= Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.jcenterRepo
)