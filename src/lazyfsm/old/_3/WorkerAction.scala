package lazyfsm.old._3

import java.util.concurrent.CompletableFuture

import lazyfsm.external.Messages.Message

trait WorkerAction { }

case class CreateWorker(task: Runnable, beanstalkActions: List[BeanstalkAction]) extends WorkerAction {
  /*
   * This method is represents a lazy I/O (forking of a thread)
   *
   * It means that when a WorkerAction class is instantiated (e.g. CreateWorker),
   * the I/O is only described but not executed as a side-effect.
   *
   * And when the method run is invoked, then the I/O starts executed and
   * a client is returned a future.
   */
  def fork = CompletableFuture runAsync task
}

case object KillWorker extends WorkerAction {
  def apply(worker: CompletableFuture[Void]) = kill(worker)
  
  def kill(worker: CompletableFuture[Void]): Unit = worker.cancel(true)
}

case object NoWorkerAction extends WorkerAction { }

object WorkerAction {
  //TODO: Implement!
  def createTask(msg: Message): Runnable = ???
}