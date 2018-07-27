package lazyfsm.main

import org.scalatest.FunSuite
import lazyfsm.external.Messages._

class StateTransitionTests extends FunSuite {
  val jobId = 314
  val subtask: Runnable = () => ()
  val msgToWork = (_: Message) => subtask
  val idleState = Idle(msgToWork)
  val uploadingState = Uploading(msgToWork)

  //--------------------------------------------Idle-----------------------------------------------

  test("cancel:: idle -> ([delete job], no forking, idle)") {
    val (actions, newState) = Controller.dispatch(idleState, Message(Cancel, jobId))

    assert(actions == List(UploaderDelete(jobId)))
    assert(newState == idleState)
  }

  test("delete:: idle -> ([msg to archiver, delete job], no forking, idle") {
    val msg = Message(Delete, jobId)

    val (actions, newState) = Controller.dispatch(idleState, msg)

    assert(actions == List(ArchiverSend(msg), UploaderDelete(jobId)))
    assert(newState == idleState)
  }

  test("upload:: idle -> ([], (CreateWorker, [delete job]), uploading") {
    val msg = Message(Upload, jobId)

    val (actions, newState) = Controller.dispatch(idleState, msg)

    assert(actions == List(CreateWorker(msgToWork(msg), List(UploaderDelete(jobId)))))
    assert(newState == uploadingState)
  }

  test("uploadDelete:: idle -> ([CreateWorker, [msg to archiver, delete job])], uploading") {
    val msg = Message(UploadDelete, jobId)

    val (actions, newState) = Controller.dispatch(idleState, msg)

    assert(actions == List(CreateWorker(msgToWork(msg), List(ArchiverSend(msg), UploaderDelete(jobId)))))
    assert(newState == uploadingState)
  }

  //------------------------------------------Uploading--------------------------------------------

  test("cancel:: uploading -> ([delete job], KillWorker, idle)") {
    val (actions, newState) = Controller.dispatch(uploadingState, Message(Cancel, jobId))

    assert(actions == List(KillWorker, UploaderDelete(jobId)))
    assert(newState == idleState)
  }

  test("delete:: uploading -> ([msg to archiver, delete job], no forking, uploading") {
    val msg = Message(Delete, jobId)

    val (actions, newState) = Controller.dispatch(uploadingState, msg)

    assert(actions == List(ArchiverSend(msg), UploaderDelete(jobId)))
    assert(newState == uploadingState)
  }

  test("upload:: uploading -> ([requeue job, delete old job], no forking, uploading") {
    val msg = Message(Upload, jobId)

    val (actions, newState) = Controller.dispatch(uploadingState, msg)

    assert(actions == List(UploaderSend(msg), UploaderDelete(msg.jobId)))
    assert(newState == uploadingState)
  }

  test("uploadDelete:: uploading -> ([requeue job, delete old job], no forking, uploading") {
    val msg = Message(UploadDelete, jobId)

    val (actions, newState) = Controller.dispatch(uploadingState, msg)

    assert(actions == List(UploaderSend(msg), UploaderDelete(msg.jobId)))
    assert(newState == uploadingState)
  }
}
