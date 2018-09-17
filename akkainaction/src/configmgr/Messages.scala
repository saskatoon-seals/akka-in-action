package configmgr

import Entities._

object Messages {
  case class Apply(delta: Delta)

  case object QueryCurrentState
  case class NewCurrentState(currentState: CurrentState)

  case object QueryTargetState
  case class NewTargetState(targetState: TargetState)
}
