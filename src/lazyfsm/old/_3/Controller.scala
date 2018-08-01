package lazyfsm.old._3

import java.util.concurrent.CompletableFuture

import lazyfsm.external.Beanstalk
import lazyfsm.external.Messages._
import lazyfsm.old._3.EffectsExecutor._
import lazyfsm.old._3.State.StateWithEffects

import scala.annotation.tailrec

object Controller extends App {
  class Connections(val uploader: Beanstalk, val archiver: Beanstalk)
  class ControllerConnections(uploader: Beanstalk, archiver: Beanstalk) extends Connections(uploader, archiver)
  class WorkerConnections(uploader: Beanstalk, archiver: Beanstalk) extends Connections(uploader, archiver)

  //*******************************************impure functions*********************************************************

  override def main(args: Array[String]): Unit = {
    eventLoop(
      Idle(WorkerAction.createTask(_)),
      new ControllerConnections(Beanstalk("crash-logs-uploader"), Beanstalk("crash-logs-archive-manager")),
      new WorkerConnections(Beanstalk("crash-logs-uploader"), Beanstalk("crash-logs-archive-manager")),
      None
    )
  }

  @tailrec
  def eventLoop(state: State, ctrlConns: ControllerConnections, workerConns: WorkerConnections, oldFuture: Option[CompletableFuture[Void]]): Unit = {
    val (beanstalkActions, workerAction, newState) = dispatch(
      state,
      ctrlConns.uploader.read() //read the next value - blocking call (side-effect)
    )

    //Perform side-effects
    val newFuture = executeWorker(oldFuture, workerAction, workerConns)
    executeBeanstalk(beanstalkActions, ctrlConns)

    //Recurse
    eventLoop(newState, ctrlConns, workerConns, newFuture)
  }

  //*************************************************pure functions*****************************************************

  /*
   * Dispatch the incoming message based on the current state
   *
   * This function is modeled after Mealy state machine and it represents the state transition
   *
   * This is a side-effectful dispatching method, because it takes a state and an input
   * and produces the next state.
   * In order to make it referentially transparent, the method would need to return an output
   * which represents the side-effect.
   *
   * The transparent function should look like:
   *
   *  dispatch :: (State, Message) => (IO[Unit], State)
   */
  //TODO: Investigate meta-programming options to generate this code based on string matching of protobufs
  def dispatch(state: State, msg: Message): StateWithEffects = msg match {
    case Message(Delete, _, _) => state.onDelete(msg)
    case Message(Cancel, _, _) => state.onCancel(msg)
    case Message(Upload, _, _) => state.onUpload(msg)
    case Message(UploadDelete, _, _) => state.onUploadDelete(msg)
  }
}
