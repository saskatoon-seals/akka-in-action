package configmgr

object Entities {
  //Delta = |TargetState - CurrentState|
  case class Delta(value: String)

  case class TargetState(value: String)
  case class CurrentState(value: String)
}
