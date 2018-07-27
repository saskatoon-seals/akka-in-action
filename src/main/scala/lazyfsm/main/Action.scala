package lazyfsm.main

import java.util.concurrent.CompletableFuture

import lazyfsm.external.Messages.Message
import lazyfsm.external.Beanstalk

trait Action { }

//-------------------------------------worker actions----------------------------------------------

case class CreateWorker(task: Runnable, beanstalkActions: List[Action]) extends Action {
  /*
   * This method is represents a lazy I/O (forking of a thread)
   *
   * It means that when a Action class is instantiated (e.g. CreateWorker),
   * the I/O is only described but not executed as a side-effect.
   *
   * And when the method run is invoked, then the I/O starts executed and
   * a client is returned a future.
   */
  def fork = CompletableFuture runAsync task
}

case object KillWorker extends Action {
  def apply(worker: CompletableFuture[Void]) = kill(worker)

  def kill(worker: CompletableFuture[Void]): Unit = worker.cancel(true)
}

//------------------------------------beanstalk actions--------------------------------------------

case class UploaderDelete(jobId: Int) extends Action {
  def apply(client: Beanstalk) = client.delete(jobId)
}

case class UploaderSend(msg: Message) extends Action {
  def apply(client: Beanstalk) = client.write(msg)
}

case class ArchiverSend(msg: Message) extends Action {
  def apply(client: Beanstalk) = client.write(msg)
}

//-----------------------------------------the rest------------------------------------------------

object WorkerAction {
  //TODO: Implement!
  def createTask(msg: Message): Runnable = ???
}