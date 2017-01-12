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



object Tar {

  import java.io.{File, FileInputStream, FileOutputStream, OutputStream}
  import java.util.zip.GZIPInputStream
  import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
  import scala.annotation.tailrec

  private val BufferSize = 2048

  def untar(tarFile: File, destinationDirectory: File): Unit ={
    destinationDirectory.mkdirs()
    extract(new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(tarFile))), destinationDirectory)
  }

  @tailrec
  def extract(tarInputStream: TarArchiveInputStream, destinationDirectory: File): Unit = {
    Option(tarInputStream.getNextTarEntry) match {

      case Some(entry) if entry.isDirectory =>
        new File(destinationDirectory, entry.getName).mkdirs()
        extract(tarInputStream, destinationDirectory)

      case Some(entry) =>
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
              case t: Throwable => //do nothing})
            }
          }
        }
        extract(tarInputStream, destinationDirectory)

      case _ =>
    }
  }

  @tailrec
  def pipe(buffer: Array[Byte], tarInputStream: TarArchiveInputStream, entryOutputStream: OutputStream, length: Int = 0): Unit = {
    tarInputStream.read(buffer, 0, BufferSize) match {
      case len if len != -1 =>
        entryOutputStream.write(buffer, 0, len)
        pipe(buffer, tarInputStream, entryOutputStream, len)
      case _ =>
    }
  }

}

//http://massapi.com/source/apache/22/03/220396420/dist/flume/1.2.0/apache-flume-1.2.0.tar.gz/apache-flume-1.2.0.tar.gz.unzip/apache-flume-1.2.0/flume-ng-tests/src/test/java/org/apache/flume/test/util/StagedInstall.java.html#257
//  private void untarTarFile(File tarFile, File destDir) throws Exception {
//    TarArchiveInputStream tarInputStream = null;
//    try {
//      tarInputStream = new TarArchiveInputStream(new FileInputStream(tarFile));
//      TarArchiveEntry entry = null;
//      while ((entry = tarInputStream.getNextTarEntry()) != null) {
//        String name = entry.getName();
//        LOGGER.debug("Next file: " + name);
//        File destFile = new File(destDir, entry.getName());
//        if (entry.isDirectory()) {
//          destFile.mkdirs();
//          continue;
//        }
//        File destParent = destFile.getParentFile();
//        destParent.mkdirs();
//        OutputStream entryOutputStream = null;
//        try {
//          entryOutputStream = new FileOutputStream(destFile);
//          byte[] buffer = new byte[2048];
//          int length = 0;
//          while ((length = tarInputStream.read(buffer, 0, 2048)) != -1) {
//            entryOutputStream.write(buffer, 0, length);
//          }
//        } catch (Exception ex) {
//          LOGGER.error("Exception while expanding tar file", ex);
//          throw ex;
//        } finally {
//          if (entryOutputStream != null) {
//            try {
//              entryOutputStream.close();
//            } catch (Exception ex) {
//              LOGGER.warn("Failed to close entry output stream", ex);
//            }
//          }
//        }
//      }
//    } catch (Exception ex) {
//      LOGGER.error("Exception caught while untarring tar file: "
//        + tarFile.getAbsolutePath(), ex);
//      throw ex;
//    } finally {
//      if (tarInputStream != null) {
//        try {
//          tarInputStream.close();
//        } catch (Exception ex) {
//          LOGGER.warn("Unable to close tar input stream: "
//            + tarFile.getCanonicalPath(), ex);
//        }
//      }
//    }
//
//  }
