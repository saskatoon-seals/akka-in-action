package faulttolerance.withactors

import java.nio.file.{Files, Paths}
import scala.collection.JavaConverters._
import akka.actor.{Actor, ActorRef, Props}

import LogParserActor._
import DbWriterActor._

class LogParserActor(fileUri: String, dbWriterSupervisor: ActorRef) extends Actor {
  override def receive: Receive = {
    case LogFile => Files
      .lines(Paths.get(fileUri))
      .iterator()
      .asScala
      .map((line: String) => line.split('-'): Array[String])
      .map(splitLine => Line(fileUri, splitLine(0), splitLine(1)))
      .foreach(dbWriterSupervisor ! _) //sends messages to dbWriter in fire-and-forget style
      //if dbWriterSupervisor is busy waiting for its child dbWriter to stop&start, it should
      //queue the messages
  }
}

object LogParserActor {
  def props(fileUri: String, dbWriterSupervisor: ActorRef) = Props(
    new LogParserActor(fileUri, dbWriterSupervisor)
  )

  case object LogFile
}
