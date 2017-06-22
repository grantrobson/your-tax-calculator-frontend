libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-compress" % "1.13" % "provided",
  "commons-io" % "commons-io" % "2.5" % "provided")

lazy val decompressTgz = taskKey[Unit]("Decompress TGZ artefact and write output to configured dir")

decompressTgz := {

  implicit val log = streams.value.log

  class PayeEstimator(override val artefactVersion: String) extends SJSTarArtefact{
      override val artefactName: String = "paye-estimator"
      override val origin = Keys.target.value / "extracted" / artefactVersion / tgz
      override val destinationDir: File = Keys.baseDirectory.value / "public" / "javascripts" / artefactVersion
  }

  val v1_6_0 = new PayeEstimator("1.6.0")
  val v1_7_0 = new PayeEstimator("1.7.0")

  v1_6_0.downloadExtractAndMove()
  v1_7_0.downloadExtractAndMove()

}

compile in Compile <<= (compile in Compile).dependsOn(decompressTgz)
