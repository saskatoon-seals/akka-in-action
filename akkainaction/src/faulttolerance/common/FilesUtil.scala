package faulttolerance.common

import java.io.{File, PrintWriter}

object FilesUtil {
  def fileIdToUri(fileId: String) = fileId.replace('-', '/')

  def fileUriToId(fileUri: String) = fileUri.replace('/', '-')

  def writeToInputFile(size: Int, event: String, fileUri: String): Unit = {
    val pw = new PrintWriter(new File(fileUri ))

    (0 until size) map { line =>
      line + " - " + event + "\n"
    } foreach { pw.write(_) }

    pw.close()
  }
}
