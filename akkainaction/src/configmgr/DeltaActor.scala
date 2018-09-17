package configmgr

import akka.actor._
import configmgr.Messages._
import configmgr.Entities._

/*
 * Stateless delta state actor
 *
 * When it receives a state from one actor it queries the other actor so it gets both
 * states to calculate the delta
 *
 * What happens if applier dies and it restarts?
 *  - queries it's stored current state from persistent
 *  - sends NewCurrentState request to this actor
 *  - NewTargetState requests were ignored because applierIsIdle was false
 *  - target state is queried and apply request is re-sent if delta > 0
 */
class DeltaActor(targetStateActor: ActorRef, applier: ActorRef) extends Actor {
  //shared mutable variable
  var applierIsIdle: Boolean = true

  override def receive: Receive = {
    /*
     * This message is received from the applier actor:
     *  - when it's done with the applying
     *  - on restart of the applier
     */
    case NewCurrentState(currentState) => {
      val delta = calculateDelta(
        //most recent target state - intermediate target states were never queued
        targetStateActor ? QueryTargetState,
        currentState
      )

      if (isZero(delta)) {
        applierIsIdle = true
      } else {
        sender() ! Apply(delta)
      }
    }

    /*
     * This message is received from the targetStateActor
     *
     * The messages aren't processed when the applier is working - which guarantees that only the
     * most recent target state will be applied (in the method above)
     */
    case NewTargetState(targetState) if applierIsIdle => {
      val delta = calculateDelta(
        targetState,
        applier ? QueryCurrentState
      )

      if (!isZero(delta)) {
        applier ! Apply(delta)

        applierIsIdle = false
      }
    }
  }

  //Utility functions:
  def calculateDelta(targetState: TargetState, currentState: CurrentState): Delta = ???

  def isZero(delta: Delta): Boolean = ???
}
