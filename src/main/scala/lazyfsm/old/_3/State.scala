package lazyfsm.old._3

import lazyfsm.external.Messages.Message

object State {
  type StateWithEffects = (List[BeanstalkAction], WorkerAction, State)
}

trait State {
  import State.StateWithEffects

  /*
   * Deleting the file - default method
   *
   * 1. forwards delete message to the archive-mgrd
   * 2. deletes it's own message from beanstalk
   *
   * @return the same state
   */
  def onDelete(msg: Message): StateWithEffects = (
    List(
      ArchiverSend(msg),
      UploaderDelete(msg.jobId)
    ),
    NoWorkerAction,
    this
  )

  /*
   * Canceling the work
   *
   * In either idle or uploading case this operation is fast and the job can be deleted from
   * beanstalk immediately
   */
  def onCancel(msg: Message): StateWithEffects = {
    val (workerAction, newState) = onCancel1(msg)

    (
      List(UploaderDelete(msg.jobId)),
      workerAction,
      newState
    )
  }

  //***Abstract methods***

  def onUpload(msg: Message): StateWithEffects
  def onUploadDelete(msg: Message): StateWithEffects

  def onCancel1(msg: Message): (WorkerAction, State)
}

case class Idle(createSubTask: Message => Runnable) extends State {
  def onCancel1(msg: Message)= (NoWorkerAction, this)

  def onUpload(msg: Message) = (
    List(),
    CreateWorker(
      createSubTask(msg),
      List(UploaderDelete(msg.jobId))
    ),
    Uploading(createSubTask)
  )

  def onUploadDelete(msg: Message) = (
    List(),
    CreateWorker(
      createSubTask(msg),
      List(ArchiverSend(msg), UploaderDelete(msg.jobId))
    ),
    Uploading(createSubTask)
  )
}

case class Uploading(createSubTask: Message => Runnable) extends State {
  def onCancel1(msg: Message) = (KillWorker, Idle(createSubTask))

  def onUpload(msg: Message)= onUploadDelete(msg)

  def onUploadDelete(msg: Message) = (
    List(
      UploaderSend(msg), //re-queue the message
      UploaderDelete(msg.jobId)
    ),
    NoWorkerAction,
    this
  )
}
