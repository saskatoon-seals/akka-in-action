package lazyfsm.old._3

import java.util.concurrent.CompletableFuture

import lazyfsm.old._3.Controller.{Connections, WorkerConnections}

object EffectsExecutor {
  def executeBeanstalk(actions: List[BeanstalkAction], connections: Connections): Unit =
    actions.foreach(executeBeanstalk(_, connections))

  /*
   * Interpret the effects produced by state transitions
   *
   * WARNING: The type system won't guide the implementation to pass in the correct clients (at compile-time)
   */
  def executeBeanstalk(action: BeanstalkAction, connections: Connections): Unit = action match {
    case action: UploaderDelete => action(connections.uploader)
    case action: UploaderSend => action(connections.uploader)
    case action: ArchiverSend => action(connections.archiver)
  }

  /*
   * This executes a worker in a background thread.
   *
   * It's important to note that the worker is running in parallel with the controller and both of them are
   * reading/writing to beanstalkd. That implies that the worker needs it's own connections to beanstalk.
   *
   * The method returns a handle to a running computation or None
   */
  def executeWorker(future: Option[CompletableFuture[Void]], action: WorkerAction, workerConns: WorkerConnections): Option[CompletableFuture[Void]] = (future, action) match {
    //this function was called only if there's currently a sub-task executing, so then simply preserve it
    case (_, NoWorkerAction) => future // future is None, or Some(f)

    case (Some(f), action@KillWorker) => { action(f); None }

    //forks a sub-task and chains the beanstalk actions (callbacks) after the sub-task is completed
    case (None, action@CreateWorker(_, beanstalkActions)) => Some(
      beanstalkActions.foldLeft(action.fork) { (acc, bAction) =>
        acc thenRun { () => executeBeanstalk(bAction, workerConns) }
      }
    )

    /*
     * Two cases are illegal:
     *  - trying to kill a sub-task which doesn't exist (uploader is in an idle state)
     *  - trying to fork a new sub-task while there's an upload already executing (against the requirements)
     */
    case (_, _) => throw new IllegalStateException("Current state must not produce a given action")
  }
}
