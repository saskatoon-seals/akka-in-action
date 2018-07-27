package lazyfsm.imperative

import java.util.concurrent.CompletableFuture

import lazyfsm.external.Beanstalk
import lazyfsm.external.Messages._

import scala.annotation.tailrec

class Connections(val uploader: Beanstalk, val archiver: Beanstalk)
class ControllerConnections(uploader: Beanstalk, archiver: Beanstalk) extends Connections(uploader, archiver)
class WorkerConnections(uploader: Beanstalk, archiver: Beanstalk) extends Connections(uploader, archiver)

case class Controller(var futures: List[CompletableFuture[Void]], controller: ControllerConnections, worker: WorkerConnections) extends App {
  override def main(args: Array[String]): Unit = {
    val controllerConnections = new ControllerConnections(
      Beanstalk("crash-logs-uploader"),
      Beanstalk("crash-logs-archive-manager")
    )

    val workerConnections = new WorkerConnections(
      Beanstalk("crash-logs-uploader"),
      Beanstalk("crash-logs-archive-manager")
    )

    Controller(List(), controllerConnections, workerConnections).eventLoop()
  }

  @tailrec
  final def eventLoop(): Unit = {
    val msg = controller.uploader.read()

    msg match {
      case Message(Delete, _, _) => {
        controller.archiver.write(msg)
        controller.uploader.delete(msg.jobId)
      }

      case Message(Cancel, _, _) => {
        //In the future it may be more than 1
        if (futures.length == 1) {
          futures(0).cancel(true)
          this.futures = futures.drop(1)
        }

        controller.uploader.delete(msg.jobId)
      }

      case Message(Upload, _, _) => uploadHelper(
          msg,
          List{ () => worker.uploader.delete(msg.jobId) }
      )

      case Message(UploadDelete, _, _) => uploadHelper(
        msg,
        List(
          () => worker.archiver.write(msg),
          () => worker.uploader.delete(msg.jobId)
        )
      )
    }

    eventLoop()
  }

  def uploadHelper(msg: Message, callbacks: List[() => Unit]) =
    if (futures.isEmpty)
      futures = List(
        CompletableFuture runAsync { () => {
          uploadFile(msg)
          callbacks.foreach(_())
        }}
      )
    else
      controller.uploader.delete(msg.jobId)

  //TODO: Implement!
  def uploadFile(msg: Message): Unit = ???
}
