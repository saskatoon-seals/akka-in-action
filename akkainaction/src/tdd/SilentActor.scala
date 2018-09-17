package akkainaction.tdd

import akka.actor.{Actor, ActorRef}

class SilentActor extends Actor {
  import SilentActor._

  private var state = Vector[String]()

  //The type Receive is a partial function
  override def receive: Receive = {
    //copy state and append an element to the new copy
    case SilentMessage(value) => state = state :+ value

    //Vector[String] is sent without being wrapped into a message case class
    case GetState(sender) => sender ! state
  }

  def getState: Vector[String] = state
}

object SilentActor {
  case class SilentMessage(value: String)

  //message class used for the multi-threaded testing (based on message passing)
  case class GetState(sender: ActorRef)
}