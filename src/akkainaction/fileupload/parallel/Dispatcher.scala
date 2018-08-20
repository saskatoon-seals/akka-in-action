package akkainaction.fileupload.parallel

import akka.actor.{Actor, ActorRef}
import akkainaction.fileupload.Protocol._

import scala.util.Random

class Dispatcher(archiver: ActorRef) extends Actor {
  override def receive: Receive = {
    case Upload(jobId, fileUri) => uploadHelper(fileUri, jobId, false)

    case Cancel(jobId, fileUri) => {
      //sends message to a child actor if it exists
      context.child(fileUri) map { _ ! Cancel }

      self ! Completed(jobId)
    }

    case Delete(jobId, fileUri) => {
      archiver ! Delete(Random.nextInt(), fileUri)

      self ! Completed(jobId)
    }

    case UploadDelete(jobId, fileUri) => uploadHelper(fileUri, jobId, true)

    case Completed(jobId) => //TODO: Delete job from beanstalk
  }

  def uploadHelper(fileUri: FileUri, jobId: Int, includeDelete: Boolean): Unit = {
    context.child(fileUri) match {
      case Some(_) => ??? //TODO: Re-queue the message

      //creates an actor and sends it a message to start doing the work
      case None => {
        val worker = context.actorOf(
          Worker.props(fileUri, jobId, includeDelete),
          fileUri
        )

        worker ! Start

        sender() ! UploadStarted(fileUri)
      }
    }
  }
}
