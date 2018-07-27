package lazyfsm.old._2

import java.util.concurrent.CompletableFuture

import lazyfsm.external.Messages.Message

object State {
  type StateWithEffects = (List[BeanstalkAction], State)
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
    this
  )

  /*
   * Canceling the work
   *
   * In either idle or uploading case this operation is fast and the job can be deleted from
   * beanstalk immediately
   */
  def onCancel(msg: Message): StateWithEffects = (
    List(UploaderDelete(msg.jobId)),
    onCancel1(msg)
  )

  //***Abstract methods***

  def onUpload(msg: Message): StateWithEffects
  def onUploadDelete(msg: Message): StateWithEffects

  def onCancel1(msg: Message): State
}

case object Idle extends State {
  def onCancel1(msg: Message)= this

  //TODO: even forking a sub-task is a side-effect
  def onUpload(msg: Message) = onUploadHelper(
    msg.jobId,
    CompletableFuture runAsync Uploader.createTask(msg)
  )

  //TODO: the task within thenRun is problematic - side-effect
  def onUploadDelete(msg: Message) = onUploadHelper(
    msg.jobId,
    CompletableFuture runAsync Uploader.createTask(msg) //thenRun { () => aClient.write(msg)}
  )

  //***Helper methods***

  //TODO: the task within thenRun is problematic - side-effect
  def onUploadHelper(jobId: Int, task: CompletableFuture[Void]) = {
    val newState = new Uploading(
      task //thenRun { () => uClient.delete(jobId) }
    )

    (List(NoAction), newState)
  }
}

case class Uploading(task: CompletableFuture[Void]) extends State {
  //TODO: task.cancel is a side-effect (it's undeclared in the function signature)
  def onCancel1(msg: Message): State = {
    task.cancel(true)

    Idle
  }

  def onUpload(msg: Message)= onUploadDelete(msg)

  def onUploadDelete(msg: Message) = (
    List(
      UploaderSend(msg), //re-queue the message
      UploaderDelete(msg.jobId)
    ),
    this
  )
}
