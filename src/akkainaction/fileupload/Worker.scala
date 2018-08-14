package akkainaction.fileupload

import java.io.InputStreamReader

import Protocol._
import akka.actor.{Actor, PoisonPill, Props}

class Worker(fileUri: FileUri) extends Actor {
  import Worker._

  override def receive: Receive = {
    case Start => self ! Continue(null, 0, 1000)

    //TODO: Test that ActorRef isn't a part of parent's context anymore
    case Cancel => self ! PoisonPill

    case Continue(source, offset, sizeDelta) => {
      /*
       * TODO:
       *  - Upload a chunk of file of size delta
       *  - Upload the file with "Transfer-Encoding: chunked" for HTTP
       */

      //Recursively continue uploading
      if (source.ready())
        self ! Continue(source, offset + sizeDelta, sizeDelta)
      else
        //The actor is no longer required after the upload has finished
        self ! PoisonPill
    }
  }
}

object Worker {
  def props(fileUri: FileUri) = Props(
    new Worker(fileUri)
  )

  case class Continue(source: InputStreamReader, offset: Int, sizeDelta: Int)
}
