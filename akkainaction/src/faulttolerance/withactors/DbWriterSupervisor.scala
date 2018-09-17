package faulttolerance.withactors

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, PoisonPill, Props, Terminated}
import faulttolerance.common.Exceptions.{DbBrokenConnectionException, DbNodeDownException}
import DbWriterActor.Line

class DbWriterSupervisor(var dbUrls: Vector[String]) extends Actor with ActorLogging {
  var dbWriter = attachDbWriter()

  /*
   * What about time between Stopping the dbWriter and before Terminated is emitted
   * from the dbWriter:
   *  - dbWriterSupervisor isn't busy and will forward messages to the dead dbWriter actor
   *  - Line messages are queued in the "in-between" Stop&Terminated events
   *
   * Solution:
   *  - the sender of Line messages should pause in the "in-between" time
   */
  override def receive: Receive = {
    case line: Line => {
//      log.info("DbWriterSupervisor is forwarding the message to the dbWriter")
//      log.info(line.toString)

      dbWriter ! line
    }

    case Terminated(_) => {
      if (dbUrls.isEmpty) {
        log.error("DbWriterSupervisor is terminating itself.")

        self ! PoisonPill
      }
      else {
        log.info("DbWriterSupervisor is replacing a dbWriter reference with a new actor.")

        dbWriter = attachDbWriter()
      }
    }
  }

  override def supervisorStrategy = OneForOneStrategy() {
    case _: DbBrokenConnectionException => Restart //DbWriter will encounter this
    case _: DbNodeDownException => Stop //DbWriter will encounter this
  }

  //-----------------------------------------helpers-----------------------------------------------

  //NOTE: side-effecting method (modifies context and dbUrls)
  def attachDbWriter(): ActorRef = {
    val newDbWriter = context.actorOf(
      DbWriterActor.props(dbUrls.head),
      "db-watcher"
    )

    dbUrls = dbUrls.tail

    //listen for the Terminated event
    context.watch(newDbWriter)

    newDbWriter
  }
}

object DbWriterSupervisor {
  def props(dbUrls: Vector[String]) = Props(
    new DbWriterSupervisor(dbUrls)
  )
}
