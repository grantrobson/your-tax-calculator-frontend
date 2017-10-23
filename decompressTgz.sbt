import sbt.Def

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-compress" % "1.13" % "provided",
  "commons-io" % "commons-io" % "2.5" % "provided")

resourceGenerators in Assets += Def.task {

  implicit val log = streams.value.log

  class PayeEstimator(override val artefactVersion: String, versionDir: Option[String] = None) extends SJSTarArtefact {
      override val artefactName: String = "paye-estimator"
      override val origin = Keys.target.value / "extracted" / artefactVersion / tgz
      override val destinationDir: File = (resourceManaged in Assets).value / "javascripts" / versionDir.getOrElse(artefactVersion)
  }

  val v1_7_0 = new PayeEstimator("1.8.0", Option("1.6.0"))

  v1_7_0.downloadAndExtract()

}.taskValue
