import java.nio.file.{Files, StandardCopyOption}

val artefactName = "paye-estimator"
val artefactVersion = "1.3.0"


val artefactRepositoryName = s"${artefactName}_sjs0.6_2.11-$artefactVersion"
val tgz = s"$artefactRepositoryName.tgz"
val tgzUrl = s"https://dl.bintray.com/hmrc/releases/uk/gov/hmrc/${artefactName}_sjs0.6_2.11/$artefactVersion/$tgz"

libraryDependencies ++= Seq("uk.gov.hmrc" % artefactName % artefactVersion from tgzUrl,
  "org.apache.commons" % "commons-compress" % "1.13" % "provided")

lazy val decompressTgz = taskKey[Unit]("Decompress TGZ artefact and write output to configured dir")

decompressTgz := {
  implicit val log = streams.value.log

  lazy val root = Keys.baseDirectory.value

  def finalDestinationDirectory(childDir: Option[String] = None, destRoot : File = root / "public" / "javascripts") = childDir match {
    case Some(dir) =>  destRoot / dir
    case _ => destRoot
  }

  val versionedArtefactName = s"$artefactName-$artefactVersion"
  val tgzFile = root / "lib_managed" / "tgzs" / "uk.gov.hmrc" / s"$artefactName" / s"$versionedArtefactName.tgz"
  val extractedTo = Keys.target.value / "extracted" / artefactVersion

  val destination = finalDestinationDirectory(Some(artefactVersion))
  if (java.nio.file.Files.notExists(destination.toPath)) {
    log.debug(s"Tar '$tgzFile' will be extracted to ${extractedTo.toPath}")

    Tar.untar(tgzFile, extractedTo)
    Files.move(extractedTo.toPath, destination.toPath, StandardCopyOption.ATOMIC_MOVE)
  } else {
    log.debug(s"Tar '$tgzFile' exists, no need to download.")
  }

}

// removing this binding until we resolve the backwards compatibility issues
//compile in Compile <<= (compile in Compile).dependsOn(decompressTgz)
