package akkainaction.fileupload.parallel

import java.io.InputStreamReader

import akka.actor.{Actor, PoisonPill, Props}
import akkainaction.fileupload.Protocol.{Cancel, Completed, FileUri, Start}

//The class is almost untestable because the way it's implemented
class Worker(fileUri: FileUri, jobId: Int, includeDelete: Boolean) extends Actor {
  import Worker._
  import akkainaction.fileupload.Protocol.Delete

  override def receive: Receive = {
    //this method has a side-effect because Continue has a side-effect
    case Start => self ! Continue(null, 0, 1000)

    case Cancel => {
      sender() ! Completed(jobId)

      self ! PoisonPill
    }

    case Continue(source, offset, sizeDelta) => {
      uploadChunk(source, offset, sizeDelta)

      //Recursively continue uploading
      if (source.ready())
        self ! Continue(source, offset + sizeDelta, sizeDelta)
      else {
        if (includeDelete)
          sender() ! Delete(jobId, fileUri)
        else
          sender() ! Completed(jobId)

        //The actor is no longer required after the upload has finished
        self ! PoisonPill
      }
    }
  }
}

object Worker {
  def props(fileUri: FileUri, jobId: Int, includeDelete: Boolean) = Props(
    new Worker(fileUri, jobId, includeDelete)
  )

  case class Continue(source: InputStreamReader, offset: Int, sizeDelta: Int)

  /*
   * TODO:
   *  - Upload a chunk of file of size delta
   *  - Upload the file with "Transfer-Encoding: chunked" for HTTP
   */
  def uploadChunk(source: InputStreamReader, offset: Int, sizeDelta: Int): Unit = ???
}
