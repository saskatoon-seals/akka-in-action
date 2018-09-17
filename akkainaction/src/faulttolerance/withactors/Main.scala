package withactors

import akka.actor.{ActorSystem, Props}

import faulttolerance.withactors.FileWatcherActor
import faulttolerance.withactors.FileWatcherActor.NewFile
import faulttolerance.common.FilesUtil._

object Main extends App {
  //----------------------------------------prepare files------------------------------------------

  val fileUri1 = "/tmp/file1.txt"
//  val fileUri2 = "/tmp/file2.txt"

  writeToInputFile(100, "Ales", fileUri1)
//  writeToInputFile(150, "Maja", fileUri2)

  //---------------------------------------prepare actors------------------------------------------

  val system = ActorSystem("log-processing-system")

  val fileWatcher = system.actorOf(
    Props(new FileWatcherActor(
      //files to read:
      Vector(
        fileUriToId(fileUri1)
      ),
      //databases to write to:
      Vector(
        "db-example-1",
        "db-example-2"
      )
    )),
    "file-watcher"
  )

  //-------------------------------------------execute---------------------------------------------

  //These two files should be processed in parallel
  fileWatcher ! NewFile(fileUriToId(fileUri1))
//  fileWatcher ! NewFile(fileUriToId(fileUri2))
}