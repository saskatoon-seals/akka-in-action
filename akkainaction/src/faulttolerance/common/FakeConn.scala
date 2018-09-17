package faulttolerance.common

import faulttolerance.common.Exceptions._

import scala.util.Random

class FakeConn(url: String) {
  val MAX = 50
  var numOfWrites: Int = 0

  var connectionBroken = false
  var databaseBroken = false

  /**
    * Writes a map to a database.
    *
    * @param map the map to write to the database.
    * @throws DbBrokenConnectionException when the connection is broken. It might be back later
    * @throws DbNodeDownException         when the database Node has been removed from the database cluster. It will never work again.
    */
  def write(entry: (String, String, String)): Unit = synchronized {
    numOfWrites += 1

    if (numOfWrites == MAX)
      connectionBroken = true
//    else if(numOfWrites == Random.nextInt(MAX))
//      databaseBroken = true

    if (connectionBroken) {
      throw new DbBrokenConnectionException(
        "Failed writing to db after " + numOfWrites.toString + " successful writes."
      )
    } else if (databaseBroken) {
      throw new DbNodeDownException(
        "Database " + url + " went down after " + numOfWrites.toString + " successful writes."
      )
    } else {
      println(entry)
    }
  }

  def close(): Unit = {
    println("Connection " + url + " closed.")
  }
}