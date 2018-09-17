package lazyfsm.main

import lazyfsm.external.Messages.Message

trait State {
  /*
   * Deleting the file - default method
   *
   * 1. forwards delete message to the archive-mgrd
   * 2. deletes it's own message from beanstalk
   *
   * @return the same state
   */
  def onDelete(msg: Message): (List[Action], State) = (
    List(
      ArchiverSend(msg),
      UploaderDelete(msg.jobId)
    ),
    this
  )

  //***Abstract methods***

  def onCancel(msg: Message): (List[Action], State)
  def onUpload(msg: Message): (List[Action], State)
  def onUploadDelete(msg: Message): (List[Action], State)
}

case class Idle(createSubTask: Message => Runnable) extends State {
  def onCancel(msg: Message)= (List(UploaderDelete(msg.jobId)), this)

  def onUpload(msg: Message) = onUploadHelper(msg, List(UploaderDelete(msg.jobId)))

  def onUploadDelete(msg: Message) =
    onUploadHelper(msg, List(ArchiverSend(msg), UploaderDelete(msg.jobId)))

  def onUploadHelper(msg: Message, cleanupActions: List[Action]) = (
    List(
      CreateWorker(
        createSubTask(msg),
        cleanupActions
      )
    ),
    Uploading(createSubTask)
  )
}

case class Uploading(createSubTask: Message => Runnable) extends State {
  def onCancel(msg: Message) = (
    List(KillWorker, UploaderDelete(msg.jobId)),
    Idle(createSubTask)
  )

  def onUpload(msg: Message)= onUploadDelete(msg)

  def onUploadDelete(msg: Message) = (
    List(
      UploaderSend(msg), //re-queue the message
      UploaderDelete(msg.jobId)
    ),
    this
  )
}
