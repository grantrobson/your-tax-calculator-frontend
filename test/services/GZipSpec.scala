/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import java.io.{BufferedInputStream, InputStream}

import org.apache.commons.compress.archivers.{ArchiveEntry, ArchiveInputStream, ArchiveStreamFactory}
import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveInputStream}
import org.apache.commons.compress.compressors.CompressorStreamFactory
import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.play.test.UnitSpec

import scala.util.{Failure, Success, Try}

class GZipSpec  extends UnitSpec with ScalaFutures {

  "check" in {

    import better.files._
    import java.io.{File => JFile}


    val artefactName = "paye-estimator"
    val artefactVersion = "1.2.0"
    val artefactRepositoryName = s"${artefactName}_sjs0.6_2.11-$artefactVersion"
    val tgz = s"$artefactRepositoryName.tgz"
    val tgzUrl = s"https://dl.bintray.com/hmrc/releases/uk/gov/hmrc/${artefactName}_sjs0.6_2.11/$artefactVersion/$tgz"


    val baseDir = "/Users/duncancrawford/development/workspaces/hmrc/next-gen-consumer/your-tax-calculator-frontend"

//    lazy val tgzFile =  new JFile(s"$baseDir/lib_managed/tgzs/uk.gov.hmrc/$artefactName/test.tgz")
    //  lazy val extractedPath = (Keys.target.value / s"$artefactName-opt.js").toPath
//    lazy val extractedPath = new JFile(s"$baseDir/public/test.js").toPath

//    java.nio.file.Files.copy(new GZIPInputStream(new FileInputStream(tgzFile)), extractedPath, StandardCopyOption.REPLACE_EXISTING)

//    File(tgzFile.toPath).newInputStream.gzipped.buffered.lines.take(10)

    val tar : File = s"$baseDir/lib_managed/tgzs/uk.gov.hmrc/$artefactName/test.tgz".toFile
    val file2 : File = s"$baseDir/public/testBF.js".toFile
    for {
      in <- tar.newInputStream.gzipped.buffered.autoClosed
      out <- file2.newOutputStream.autoClosed
    } in.pipeTo(out)


    Tar.untar(new JFile(s"$baseDir/lib_managed/tgzs/uk.gov.hmrc/$artefactName/test.tgz"), new JFile(s"$baseDir/public/new"))

  }


}
