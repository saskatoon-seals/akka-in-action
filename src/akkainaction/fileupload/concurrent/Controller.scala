package akkainaction.fileupload.concurrent

import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout

import akkainaction.fileupload.Protocol.{Cancel, Delete, Upload}
import akkainaction.fileupload.concurrent.FilesUploader.{Start, IsUploading}

import scala.concurrent.ExecutionContextExecutor

class Controller(filesUploader: ActorRef, timeout: Timeout, executor: ExecutionContextExecutor) extends Actor {
  implicit val requestTimeout = timeout
  implicit def executionContext = executor

  override def receive: Receive = {
    case Upload(fileUri) => (filesUploader ? IsUploading(fileUri)) //ask == '?'
      .mapTo[Boolean]
      .foreach(isUploading => {
        if (isUploading)
          ??? //TODO: re-queue the job
        else
          filesUploader ! Start(fileUri)
      })

    //sends message to a child actor if it exists
    case Cancel(fileUri) => filesUploader ! Cancel(fileUri)

    case msg: Delete => ??? //TODO: Forward to archive-mgrd
  }
}
