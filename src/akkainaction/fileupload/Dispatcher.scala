package akkainaction.fileupload

import akka.actor.{Actor}

import Protocol._

class Dispatcher extends Actor {
  override def receive: Receive = {
    case Upload(fileUri) => context.child(fileUri) match {
      case Some(_) => ??? //TODO: Re-queue the message

      //creates an actor and sends it a message to start doing the work
      case None => context.actorOf(Worker.props(fileUri), fileUri) ! Start
    }

    //sends message to a child actor if it exists
    case Cancel(fileUri) => context.child(fileUri) map { _ ! Cancel }

    case msg: Delete => ??? //TODO: Forward to archive-mgrd
  }
}
