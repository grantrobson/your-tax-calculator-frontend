import sbt._

object FrontendBuild extends Build with MicroService {

  val appName = "your-tax-calculator-frontend"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()

}

private object AppDependencies {
//  import play.PlayImport._
  import play.core.PlayVersion

  private val microserviceBootstrapVersion = "5.4.0"
  private val playHealthVersion = "2.0.0"
  private val playJsonLoggerVersion = "3.0.0"
  private val frontendBootstrapVersion = "7.5.0"
  private val govukTemplateVersion = "5.0.0"
  private val playUiVersion = "5.2.0"
  private val playPartialsVersion = "5.2.0"
  private val playAuthorisedFrontendVersion = "5.8.0"
  private val playConfigVersion = "3.0.0"
  private val hmrcTestVersion = "2.0.0"
  private val scalaTestVersion = "2.2.6"
  private val pegdownVersion = "1.6.0"

  val compile = Seq(
//    ws,
    "uk.gov.hmrc" %% "microservice-bootstrap" % microserviceBootstrapVersion,
    "uk.gov.hmrc" %% "frontend-bootstrap" % frontendBootstrapVersion,
    "uk.gov.hmrc" %% "play-partials" % playPartialsVersion,
    "uk.gov.hmrc" %% "play-authorised-frontend" % playAuthorisedFrontendVersion,
    "uk.gov.hmrc" %% "play-config" % playConfigVersion,
    "uk.gov.hmrc" %% "play-json-logger" % playJsonLoggerVersion,
    "uk.gov.hmrc" %% "govuk-template" % govukTemplateVersion,
    "uk.gov.hmrc" %% "play-health" % playHealthVersion,
    "uk.gov.hmrc" %% "play-ui" % playUiVersion
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
        "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
        "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "org.jsoup" % "jsoup" % "1.8.1" % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % scope
      )
    }.test
  }

  def apply() = compile ++ Test()
}


