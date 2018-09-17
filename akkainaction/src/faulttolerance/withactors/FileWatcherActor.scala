package faulttolerance.withactors

import akka.actor.Actor

import FileWatcherActor._
import LogParserActor._
import faulttolerance.common.FilesUtil.fileIdToUri

/*
 * This actor is doing 2 things (that should be separated out):
 *  - handles messages
 *  - handles errors
 */
class FileWatcherActor(fileIds: Vector[String], var dbUrls: Vector[String]) extends Actor {
  //creates a map: [(fileId, logParser)]
  val logParsers = Map(
    fileIds map { fileId =>
      (
        fileId,
        //creates logParser actor:
        context.actorOf(
          LogParserActor.props(
            //log-parser-actor is responsible for a specific fileUri
            fileIdToUri(fileId),
            //DbWriterSupervisor is created on FileWatcherActor's context!
            context.actorOf(
              DbWriterSupervisor.props(dbUrls),
              "supervisor"
            )
          ),
          fileId
        )
      )
    } : _*
  )

  //dispatches a message to the appropriate log-parser (for the right file-uri/id)
  override def receive: Receive = {
    case NewFile(fileId) => logParsers(fileId) ! LogFile
  }
}

object FileWatcherActor {
  case class NewFile(fileId: String)
}