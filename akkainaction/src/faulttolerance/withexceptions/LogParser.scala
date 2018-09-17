package faulttolerance.withexceptions

import java.nio.file.{Files, Paths}
import scala.collection.JavaConverters._

class LogParser(fileUri: String, dbWriter: DbWriter2) {
  def parse: Unit = Files
    .lines(Paths.get(fileUri))
    .iterator()
    .asScala
    .map((line: String) => line.split('-'): Array[String])
    .map(splitLine => (fileUri, splitLine(0), splitLine(1)))
    /*
     * Should the JedisException be handled here and the call to dbWriter made again, so that the row isn't lost?
     *
     * Certainly the log parser knows the row for which the write to redis failed.
     * Can this class re-create the connection and retry the write?
     */
    .foreach(dbWriter.write(_))
}
