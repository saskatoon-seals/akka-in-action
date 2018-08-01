package lazyfsm.external

object Messages {
  case class Message(requestType: RequestType, jobId: Int, value: String = "")

  trait RequestType {}
  case object Delete extends RequestType
  case object Cancel extends RequestType
  case object Upload extends RequestType
  case object UploadDelete extends RequestType
}
