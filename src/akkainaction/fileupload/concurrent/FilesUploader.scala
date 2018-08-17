package akkainaction.fileupload.concurrent

import java.io.InputStreamReader

import akka.actor.Actor
import akkainaction.fileupload.Protocol.{Cancel, FileUri}

/*
 * Worker may be executing multiple uploads
 *
 * But only one upload at a time - synchronously (drawback)!
 */
class FilesUploader extends Actor {
  import FilesUploader._

  var uris = List.empty[FileUri]

  override def receive: Receive = {
    case Start(fileUri) => {
      uris = fileUri :: uris

      //TODO: Use fileUri in order to create InputStream
      self ! Continue(fileUri, null, 0, 1000)
    }

    //fileUri the worker is uploading is not it's state - worker may be uploading multiple files
    case Cancel(fileUri) => self ! Continue(fileUri, null, 0, 0, true)

    //Problem: method has a side-effect of uploading a file
    case Continue(fileUri, source, offset, sizeDelta, stop) => {
      if (stop || !source.ready())
        uris = uris.filter(_ != fileUri)
      else {
        /*
         * TODO:
         *  - Upload a chunk of file of size delta
         *  - Upload the file with "Transfer-Encoding: chunked" for HTTP
         */

        self ! Continue(fileUri, source, offset + sizeDelta, sizeDelta)
      }
    }

    case IsUploading(fileUri) => uris.find(_ == fileUri).isDefined
  }
}

object FilesUploader {
  case class Start(fileUri: FileUri)
  case class IsUploading(fileUri: FileUri)
  case class Continue(fileUri: FileUri, input: InputStreamReader, offset: Int, sizeDelta: Int, stop: Boolean = false)
}