import sbt._

object AppDependencies {

  import play.core.PlayVersion
  import play.sbt.PlayImport._


  private val frontendBootstrapVersion = "8.8.0"
  private val hmrcTestVersion = "3.0.0"

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "frontend-bootstrap" % frontendBootstrapVersion
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
        "org.jsoup" % "jsoup" % "1.10.3" % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1" % scope
      )
    }.test
  }

  def apply() = compile ++ Test()
}


