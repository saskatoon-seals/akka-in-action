package akkainaction.tdd

import akka.actor.Actor

class EchoActor extends Actor {
  //sender() == reference of the sender of the last received message
  override def receive: Receive = {
    case msg: String => sender() ! msg
  }
}
