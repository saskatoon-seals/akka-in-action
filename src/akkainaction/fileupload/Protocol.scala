package akkainaction.fileupload

object Protocol {
  type FileUri = String

  //External request messages
  case class Upload(fileUri: FileUri)
  case class Cancel(fileUri: FileUri)
  case class Delete(fileUri: FileUri)

  //External responses
  case class UploadStarted(uri: FileUri)

  //Messages between dispatcher and worker
  case object Start
  case object Cancel
}
