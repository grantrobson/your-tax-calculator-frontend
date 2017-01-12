import org.apache.commons.io.FileUtils

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

object Tar {

  import java.io.{File, FileInputStream, FileOutputStream, OutputStream}
  import java.util.zip.GZIPInputStream
  import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
  import scala.annotation.tailrec

  private val BufferSize = 2048

  def untar(tarFile: File, destinationDirectory: File)(implicit log : sbt.Logger): Unit ={
    destinationDirectory.mkdirs()
    extract(new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(tarFile))), destinationDirectory)
  }

  @tailrec
  def extract(tarInputStream: TarArchiveInputStream, destinationDirectory: File)(implicit log : sbt.Logger): Unit = {
    Option(tarInputStream.getNextTarEntry) match {

      case Some(entry) if entry.isDirectory =>
        log.debug("The tar entry was a directory")
        new File(destinationDirectory, entry.getName).mkdirs()
        extract(tarInputStream, destinationDirectory)

      case Some(entry) =>
        log.debug("Extracting the tar entry file")
        val destinationFile = new File(destinationDirectory, entry.getName)
        destinationFile.getParentFile.mkdirs()
        val output = new FileOutputStream(destinationFile)
        try {
          pipe(new Array[Byte](BufferSize), tarInputStream, output)
        } finally {
          Option(output).map { os =>
            try {
              os.close
            } catch {
              case t : Throwable =>
                log.debug("Failed to close the the untar output stream")
            }
          }
        }
        extract(tarInputStream, destinationDirectory)

      case _ =>
        log.debug("No more entries in the tar")
    }
  }

  @tailrec
  def pipe(buffer: Array[Byte], tarInputStream: TarArchiveInputStream, entryOutputStream: OutputStream, length: Int = 0)(implicit log : sbt.Logger): Unit = {
    tarInputStream.read(buffer, 0, BufferSize) match {
      case len if len != -1 =>
        entryOutputStream.write(buffer, 0, len)
        pipe(buffer, tarInputStream, entryOutputStream, len)
      case _ =>
        log.debug("Finished piping data into output stream")
    }
  }

}

trait TarArtefact {
  import java.io.File
  import java.net.{HttpURLConnection, URL, URLConnection}
  import java.io.{FileOutputStream, InputStream, OutputStream}


  val artefactName : String
  val artefactVersion : String

  val artefactRepositoryOrgRootUrl : String = "https://dl.bintray.com/hmrc/releases/uk/gov/hmrc"

  def artefactRepositoryName : String
  lazy val tgz = s"$artefactRepositoryName.tgz"
  def tgzUrl : String
  val extractedTo : File

  lazy val tgzFile : File = FileUtils.copyURLToFile(tgzUrl, extractedTo)

//
//  def downloadFile(url : URL, file : File) : Unit = {
//    val conn = url.openConnection
//    try {
//      downloadFile(conn, file)
//    } finally conn match {
//      // http://dumps.wikimedia.org/ seems to kick us out if we don't disconnect.
//      case conn: HttpURLConnection => conn.disconnect
//      // But only disconnect if it's a http connection. Can't do this with file:// URLs.
//      case _ =>
//    }
//  }
//
//  protected def downloadFile(conn: URLConnection, file : File): Unit = {
//    val in = inputStream(conn)
//    try
//    {
//      val out = outputStream(file)
//      try
//      {
//        copy(in, out)
//      }
//      finally out.close
//    }
//    finally in.close
//  }
//
//  protected def inputStream(conn: URLConnection) : InputStream = conn.getInputStream
//
//  protected def outputStream(file: File) : OutputStream = new FileOutputStream(file)
//
//  def copy(in: InputStream, out: OutputStream) : Unit = {
//    val buf = new Array[Byte](1 << 20) // 1 MB
//    while (true)
//    {
//      val read = in.read(buf)
//      if (read == -1)
//      {
//        out.flush
//        return
//      }
//      out.write(buf, 0, read)
//    }
//  }
}

trait SJSTarArtefact extends TarArtefact {
  lazy val artefactRepositoryName = s"${artefactName}_sjs0.6_2.11-$artefactVersion"
  lazy val tgzUrl = s"$artefactRepositoryOrgRootUrl/${artefactName}_sjs0.6_2.11/$artefactVersion/$tgz"
}