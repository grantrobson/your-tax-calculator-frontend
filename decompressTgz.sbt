import java.io.{FileInputStream, InputStream}
import java.util.zip.GZIPInputStream

val artefactName = "paye-estimator"
val artefactVersion = "1.2.0"


val artefactRepositoryName = s"${artefactName}_sjs0.6_2.11-$artefactVersion"
val tgz = s"$artefactRepositoryName.tgz"
val tgzUrl = s"https://dl.bintray.com/hmrc/releases/uk/gov/hmrc/${artefactName}_sjs0.6_2.11/$artefactVersion/$tgz"

libraryDependencies ++= Seq("uk.gov.hmrc" % artefactName % artefactVersion from tgzUrl,
    "org.apache.commons" % "commons-compress" % "1.13" % "provided")

lazy val decompressTgz = taskKey[Unit]("Decompress TGZ artefact and write output to target dir")

decompressTgz := {
  val log = streams.value.log

  lazy val tgzFile =  Keys.baseDirectory.value / "lib_managed" / "tgzs" / "uk.gov.hmrc" / s"$artefactName" / s"$artefactName-$artefactVersion.tgz"
  lazy val extractedPath = (Keys.target.value / s"$artefactName-opt.js").toPath
  if(java.nio.file.Files.notExists(extractedPath)) {
    log.debug(s"Tar '$tgzFile' will be extracted")




    java.nio.file.Files.copy(new GZIPInputStream(new FileInputStream(tgzFile)), extractedPath)
  } else {
    log.debug(s"Tar '$tgzFile' exists, no need to download.")
  }

}


compile in Compile <<= (compile in Compile).dependsOn(decompressTgz)
