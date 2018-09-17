package faulttolerance.withexceptions

import faulttolerance.common.Exceptions.DbBrokenConnectionException
import faulttolerance.common.FakeConn

/*
 * The DbWriter is shared between multiple log processors.
 * The DbWriter hides a connection object from the log processor.
 *
 * DbWriter2 can reconnect to the server in the case of failure
 * and retry the failing write operation
 *
 * What's wrong with this?
 *
 * databaseUrl is used for recreating a connection
 */
class DbWriter2(databaseUrl: String) {
  var conn = new FakeConn(databaseUrl)

  //synchronized will block other threads while connection is being recreated
  def write(row: (String, String, String)): Unit = synchronized {
    try {
      conn.write(row)

      println("Written row: " + row)
    } catch {
      /*
       * When the exception is thrown we don't know which lines were already
       * processed.
       *
       * But does it matter?
       *
       * We have a reference to the line that caused an exception.
       * We can retry the same line and then continue. This way an error is invisible
       * to the log processor (the caller).
       */
      case e: DbBrokenConnectionException => {
        //THIS IS AN UNCLEAN CODE
        /*
         * Interesting problem happens when connection is being recreated inside
         * this error handling method, if requests from other log processors come
         * meanwhile.
         *
         * Will they execute this method with a broken connection? => YES!
         */
        println("Failed writing row: " + row)

        Thread.sleep(100)

        //reconnect to the same server
        conn.close()
        conn = new FakeConn(databaseUrl)

        //retry writing the same row - on "this" so that error handling can be reused
        this.write(row)
      }
    }
  }
}
