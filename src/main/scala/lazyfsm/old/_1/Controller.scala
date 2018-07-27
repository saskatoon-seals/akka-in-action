package lazyfsm.old._1

import lazyfsm.external.Beanstalk
import lazyfsm.external.Messages._

import scala.annotation.tailrec

object Controller extends App {
  @tailrec
  def eventLoop(state: State, client: Beanstalk): Unit = {
    eventLoop(
      dispatch(state, client.read()), //side-effect (read the next value - blocking call)
      client
    )
  }

  /*
   * Dispatch the incoming message based on the current state
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
  def dispatch(state: State, msg: Message): State = msg match {
    case Message(Delete, _, _) => state.onDelete(msg)
    case Message(Cancel, _, _) => state.onCancel(msg)
    case Message(Upload, _, _) => state.onUpload(msg)
    case Message(UploadDelete, _, _) => state.onUploadDelete(msg)
  }

  override def main(args: Array[String]): Unit = {
    val uploaderClient = Beanstalk("crash-logs-uploader")

    eventLoop(
      Idle(uploaderClient, Beanstalk("crash-logs-archiver")),
      uploaderClient
    )
  }
}
