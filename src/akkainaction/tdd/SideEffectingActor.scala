package akkainaction.tdd

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import Greeter1._

class Greeter1 extends Actor with ActorLogging {
  override def receive: Receive = {
    case Greeting(msg) => log.info("Hello {}!", msg)
  }
}

object Greeter1 {
  case class Greeting(msg: String)
}

//In the case of test listener is a testActor and otherwise it's None
class Greeter2(listener: Option[ActorRef]) extends Actor with ActorLogging {
  override def receive: Receive = {
    case Greeting(msg) => {
      val response = "Hello " + msg + "!"

      log.info(response)

      //Used for the simplicity of testing (sent optionally)
      listener.foreach(_ ! response)
    }
  }
}

object Greeter2 {
  def props(listener: Option[ActorRef] = None) = Props(
    new Greeter2(listener)
  )
}