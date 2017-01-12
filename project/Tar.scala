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