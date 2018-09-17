package faulttolerance.withexceptions

import java.io.{File, PrintWriter}

/*
   * The "main" method
   * I have 2 files and 2 threads => one-to-one mapping of files to threads
   */
object FileWatcherMain extends App {
  val fileUri1 = "/tmp/file1.txt"
  val fileUri2 = "/tmp/file2.txt"
  val databaseUrl = "/db"

  //Write input files
  writeToInputFile(100, "Ales", fileUri1)
  writeToInputFile(150, "Maja", fileUri2)

  //Domain objects creation
  val dbWriter = new DbWriter2(databaseUrl)

  //Start doing the I/O work
  val t1 = startLogParser(new LogParser(fileUri1, dbWriter))
  val t2 = startLogParser(new LogParser(fileUri2, dbWriter))

  //Wait for I/O to finish!
  t1.join()
  t2.join()

  //---------------------------------------helper methods------------------------------------------

  def startLogParser(logParser: LogParser) = {
    val t = new Thread {
      override def run = logParser.parse
    }

    t.start()

    t
  }

  /*
   * Input resources creation - files
   *
   * format: timestamp (for now just a unique integer) - event
   *
   * fileUri is the "source" identifier needed to save into DB
   */
  def writeToInputFile(size: Int, event: String, fileUri: String): Unit = {
    val pw = new PrintWriter(new File(fileUri ))

    (0 until size) map { line =>
      line + " - " + event + "\n"
    } foreach { pw.write(_) }

    pw.close()
  }
}
