package lazyfsm.main

import java.util.concurrent.CompletableFuture

import lazyfsm.main.Controller.{Connections, ControllerConnections, WorkerConnections}
import lazyfsm.external.Messages.Message

/*
 * This file is an added complexity because so that all of the other functions can be referentially transparent.
 * Some parts of it are hard to test because futures don't compare.
 */
class EffectsExecutor(ctrlConns: ControllerConnections, workerConns: WorkerConnections) {
  //-----------------------------------------------API---------------------------------------------------

  def performEffects(actions: List[Action], oldFuture: Option[CompletableFuture[Void]]) = {
    actions filter { !isWorkerAction(_) } foreach { performBeanstalkEffect(_, ctrlConns) }

    //we know there's only a single worker action in the list
    actions find { isWorkerAction(_) } flatMap { performWorkerEffect(_, oldFuture) }
  }

  def uploaderRead(): Message = ctrlConns.uploader.read();

  //--------------------------------------------helper methods---------------------------------------------------

  def performWorkerEffect(action: Action, future: Option[CompletableFuture[Void]]) =
    (action, future) match {
      case (action@KillWorker, Some(f)) => { action(f); None }

      //forks a sub-task and chains the beanstalk actions (callbacks) after the sub-task is completed
      case (action@CreateWorker(_, callbacks), None) => Some(
        callbacks.foldLeft(action.fork) { (acc, callback) =>
          acc thenRun { () => performBeanstalkEffect(callback, workerConns) }
        }
      )

      /*
       * Two cases are illegal:
       *  - trying to kill a sub-task which doesn't exist (uploader is in an idle state)
       *  - trying to fork a new sub-task while there's an upload already executing (against the requirements)
       */
      case (_, _) => throw new IllegalStateException("Current state must not produce a given action")
  }

  /**
    * Performs beanstalk effects in controller or worker thread
    *
    * This method can be thought of as a map of (BeanstalkEffect, Connection) in
    * a form of a pattern matching case statements.
    *
    * @param action - effect to perform
    * @param connections - connection to beanstalk (different groups of connections are passed in)
    */
  def performBeanstalkEffect(action: Action, connections: Connections): Unit = action match {
    /*
     * Interpret the effects produced by state transitions
     *
     * WARNING: The type system won't guide the implementation to pass in the correct clients (at compile-time)
     */
    case action: UploaderDelete => action(connections.uploader)
    case action: UploaderSend => action(connections.uploader)
    case action: ArchiverSend => action(connections.archiver)
  }

  def isWorkerAction(action: Action): Boolean =
    action.isInstanceOf[CreateWorker] || action.isInstanceOf[KillWorker.type]
}
