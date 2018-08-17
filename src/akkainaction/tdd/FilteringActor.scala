package akkainaction.tdd

import akka.actor.{Actor, ActorRef, Props}

/*
 * The actor is buffering unique messages in a buffer of size N. It's a "window" principle (discard oldest).
 *
 * It forwards unique messages (if they were unique in a "window").
 */
class FilteringActor(nextActor: ActorRef, bufferSize: Int) extends Actor {
  import FilteringActor._

  var buffer = Vector[Event]()

  override def receive: Receive = {
    case msg: Event if (!buffer.contains(msg)) => {
      buffer = (if (buffer.size < bufferSize) buffer else buffer.tail) :+ msg

      nextActor ! msg
    }
  }
}

object FilteringActor {
  def props(nextActor: ActorRef, bufferSize: Int) = Props(
    new FilteringActor(nextActor, bufferSize)
  )

  case class Event(id: Long)
}
