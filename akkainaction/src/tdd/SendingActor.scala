package akkainaction.tdd

import akka.actor.{Actor, ActorRef, Props}

class SendingActor(receiver: ActorRef) extends Actor {
  import SendingActor._

  override def receive: Receive = {
    case Sort(events) => receiver ! Sorted(events.sortBy(_.id))
  }
}

object SendingActor {
  //NOTE: Sending Actor is lazily instantiated
  def props(receiver: ActorRef) = Props(new SendingActor(receiver))

  case class Event(id: Long)
  case class Sort(events: Vector[Event])
  case class Sorted(events: Vector[Event])
}
