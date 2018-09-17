package configmgr

import akka.actor.Actor

import Messages._
import Entities._

/*
 * Hardware configuration applier
 *
 * It's idle after sending NewCurrentState and before receiving Apply messages
 * This is the time when DeltaActor can send messages to it if delta > 0 (on receiving NewTargetState)
 *
 * What if NewCurrentState messages (response) never arrives to DeltaActor?
 *  => that must not happen (otherwise that's a bug)
 *  => proper supervision and poststart hooks need to be implemented
 *  => current state needs to be stored in persistent and query on start
 */
class ConfigApplier extends Actor {
  override def receive: Receive = {
    case Apply(delta) => {
      //sender is delta state actor
      sender() ! NewCurrentState(
        applyDifference(delta)
      )
    }
  }

  //Utility functions:
  def applyDifference(delta: Delta): CurrentState = {
    //Apply the settings on hardware:
    Thread.sleep(1000)

    //New current state - ideally it's equal to the target state so that delta == 0
    CurrentState("")
  }
}
