package lazyfsm.old._2

import lazyfsm.old._2.State.StateWithEffects
import lazyfsm.external.Beanstalk
import lazyfsm.external.Messages._

import scala.annotation.tailrec

object Controller extends App {
  override def main(args: Array[String]): Unit = {
    eventLoop(
      Idle,
      Beanstalk("crash-logs-uploader"),
      Beanstalk("crash-logs-archive-manager")
    )
  }

  //*******************************************impure functions*********************************************************

  @tailrec
  def eventLoop(state: State, uploaderBeanstalk: Beanstalk, archiverBeanstalk: Beanstalk): Unit = {
    val (beanstalkActions, newState) = dispatch(
      state,
      uploaderBeanstalk.read() //read the next value - blocking call (side-effect)
    )

    beanstalkActions.foreach(execute(_, uploaderBeanstalk, archiverBeanstalk))

    eventLoop(newState, uploaderBeanstalk, archiverBeanstalk)
  }

  /*
   * Interpret the effects produced by state transitions
   *
   * WARNING: The type system won't guide the implementation to pass in the correct clients (at compile-time)
   */
  def execute(action: BeanstalkAction, uploaderBeanstalk: Beanstalk, archiverBeanstalk: Beanstalk): Unit = action match {
    case action: UploaderDelete => action.run(uploaderBeanstalk)
    case action: UploaderSend => action.run(uploaderBeanstalk)
    case action: ArchiverSend => action.run(archiverBeanstalk)
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
  def dispatch(state: State, msg: Message): StateWithEffects = msg match {
    case Message(Delete, _, _) => state.onDelete(msg)
    case Message(Cancel, _, _) => state.onCancel(msg)
    case Message(Upload, _, _) => state.onUpload(msg)
    case Message(UploadDelete, _, _) => state.onUploadDelete(msg)
  }
}
