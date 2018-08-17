package akkainaction.tdd

import akka.testkit.{CallingThreadDispatcher, EventFilter, TestKit}
import SideEffectingActorSuite._
import akka.actor.{ActorSystem, Props, UnhandledMessage}
import com.typesafe.config.ConfigFactory
import org.scalatest.WordSpecLike

class SideEffectingActorSuite extends TestKit(testSystem) with WordSpecLike with StopSystemAfterAll {
  import Greeter1._

  "The Greeter 1" must {
    "say Hello Luka! when it receives Greeting(\"Luka\") msg" in {

      //Single-threaded environment
      val props = Props[Greeter1].withDispatcher(
        CallingThreadDispatcher.Id
      )

      val greeter = system.actorOf(props)

      /*
       * Logging powermock
       *
       *  - Intercepts log messages that were logged
       *  - source isn't the receiver of a message
       */
      EventFilter
        .info("Hello Luka!", occurrences = 1)
        .intercept(greeter ! Greeting("Luka"))
    }
  }

  "The Greeter 2" must {
    "say Hello Luka! when it receives Greeting(\"Luka\") msg" in {
      val props = Greeter2.props(
        Some(testActor)
      )

      val greeter2 = system.actorOf(props)

      greeter2 ! Greeting("Luka")

      expectMsg("Hello Luka!")
    }

    "wait and see.." in {
      val greeter2 = system.actorOf(
        Greeter2.props(
          Some(testActor)
        )
      )

      system.eventStream.subscribe(
        testActor,
        classOf[UnhandledMessage]
      )

      //Greeter actor doesn't understand the message of type String
      greeter2 ! "Luka"

      expectMsg(
        UnhandledMessage("Luka", system.deadLetters, greeter2)
      )
    }
  }
}

object SideEffectingActorSuite {
  //system with configuration that attaches a test event listener
  val testSystem = {
    val config = ConfigFactory.parseString(
      """
         akka.loggers = [akka.testkit.TestEventListener]
      """
    )

    ActorSystem("testsystem", config)
  }
}
