package akkainaction.fileupload

object Protocol {
  type FileUri = String

  //External request messages
  case class Upload(jobId: Int, fileUri: FileUri)
  case class Cancel(jobId: Int, fileUri: FileUri)
  case class Delete(jobId: Int, fileUri: FileUri)
  case class UploadDelete(jobId: Int, fileUri: FileUri)

  //External responses
  case class UploadStarted(uri: FileUri)
  case class Completed(jobId: Int)

  //Messages between dispatcher and worker
  case object Start
  case object Cancel
}
