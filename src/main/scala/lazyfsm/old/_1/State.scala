package lazyfsm.old._1

import java.util.concurrent.CompletableFuture

import lazyfsm.external.Beanstalk
import lazyfsm.external.Messages.Message

abstract class State(uploaderClient: Beanstalk, archiverClient: Beanstalk) {
  /*
   * Deleting the file - default method
   *
   * 1. forwards delete message to the archive-mgrd
   * 2. deletes it's own message from beanstalk
   *
   * @return the same state
   */
  def onDelete(msg: Message): State = {
    archiverClient.write(msg)
    uploaderClient.delete(msg.jobId)

    this
  }

  /*
   * Canceling the work
   *
   * In either idle or uploading case this operation is fast and the job can be deleted from
   * beanstalk immediately
   */
  def onCancel(msg: Message): State = {
    val newState = onCancel1(msg)
    uploaderClient.delete(msg.jobId)

    newState
  }

  //***Abstract methods***

  def onCancel1(msg: Message): State
  def onUpload(msg: Message): State
  def onUploadDelete(msg: Message): State
}

case class Idle(uploaderClient: Beanstalk, archiverClient: Beanstalk) extends State(uploaderClient, archiverClient) {
  def onCancel1(msg: Message)= this

  def onUpload(msg: Message) = onUploadHelper(
    msg.jobId,
    CompletableFuture runAsync Uploader.createTask(msg)
  )

  def onUploadDelete(msg: Message) = onUploadHelper(
    msg.jobId,
    CompletableFuture runAsync Uploader.createTask(msg) thenRun { () => archiverClient.write(msg) }
  )

  //***Helper methods***

  def onUploadHelper(jobId: Int, task: CompletableFuture[Void]) = new Uploading(
    uploaderClient, archiverClient,
    task.thenRun(() => uploaderClient.delete(jobId))
  )
}

case class Uploading(uploaderClient: Beanstalk, archiverClient: Beanstalk, task: CompletableFuture[Void])
  extends State(uploaderClient, archiverClient) {

  def onCancel1(msg: Message): State = {
    //TODO: Cancel only the task that is uploading the file for which the msg tells it to cancel, otherwise noop
    task.cancel(true);

    Idle(uploaderClient, archiverClient)
  }

  def onUpload(msg: Message): State = onUploadDelete(msg)

  def onUploadDelete(msg: Message): State = {
    uploaderClient.write(msg) //re-queue the message
    uploaderClient.delete(msg.jobId)

    this
  }
}
