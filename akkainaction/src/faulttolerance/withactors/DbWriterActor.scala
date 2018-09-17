package faulttolerance.withactors

import akka.actor.{Actor, ActorLogging, Props}
import DbWriterActor._
import faulttolerance.common.FakeConn

//This is the actor that crashes on failing write and gets restarted by FileWatcherActor supervisor
class DbWriterActor(dbUrl: String) extends Actor with ActorLogging {
  val conn = new FakeConn(dbUrl)

  log.info("Created a db-writer actor connected to " + dbUrl + " database.")

  override def receive: Receive = {
    case line@Line(fileUri, timestamp, event) => conn.write(
      (fileUri, timestamp, event)
    )
  }

  override def postStop() = conn.close()

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    message foreach(self ! _)

    //the hook finishes off with the common logic of killing all of its children actors
    super.preRestart(reason, message)
  }
}

object DbWriterActor {
  case class Line(fileUri: String, timestamp: String, event: String)

  def props(dbUrl: String) = Props(
    new DbWriterActor(dbUrl)
  )
}