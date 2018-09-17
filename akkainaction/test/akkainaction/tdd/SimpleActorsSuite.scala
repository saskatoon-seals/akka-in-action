package akkainaction.tdd

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{MustMatchers, WordSpecLike}

import scala.util.Random

class SimpleActorsSuite extends TestKit(ActorSystem("testsystem")) //provides ActorSystem for testing
  with WordSpecLike //test can be specified as strings
  with MustMatchers //"must" assertions
  with ImplicitSender //for the echo actor tests (automatic setting of the testActor as a sender)
  with StopSystemAfterAll { //actor system is stopped after ALL of the tests finish

  "A Silent Actor must change state when it receives a msg" must { //top-level spec
    import SilentActor._

    "in a single threaded case" in { //test case no. 1
      //prepare
      val silentActor = TestActorRef[SilentActor] //is this just for single-threaded tests?

      //execute
      silentActor ! SilentMessage("whisper") //fire-and-forget

      //verify
      silentActor.underlyingActor.getState must (contain("whisper"))  //TODO: check how "must" works
    }

    "in a multi threaded case" in { //test case no. 2
      val silentActor = system.actorOf(
        Props[SilentActor], //creates an actor using its default constructor
        "s3"
      )

      /*
       * The order of arrival of messages may not be the same as the order of sending
       * Assertion may be incorrect because it's expecting the same order of sending and arrival
       */
      silentActor ! SilentMessage("whisper 1")
      silentActor ! SilentMessage("whisper 2")

      silentActor ! GetState(testActor) //testActor is a part of TestKit
      expectMsg(Vector("whisper 1", "whisper 2"))  //blocks the testActor to receive response
    }
  }

  "A Sending Actor" must {
    import SendingActor._

    "send a message to a receiving actor after it's done" in {
      //prepare
      val sendingActor = system.actorOf(props(testActor), "sendingActor")

      val size = 100
      val inputEvents = (0 until size).map { _ =>
        Event(Random.nextInt(9999))
      } toVector

      //execute
      sendingActor ! Sort(inputEvents)

      //verify
      expectMsgPF() {
        case Sorted(outputEvents) => {
          outputEvents.size must be(size)

          inputEvents.sortBy(_.id) must be(outputEvents)
        }
      }
    }
  }

  "A Filtering Actor" must {
    import FilteringActor._

    "filter out some messages" in {
      //Prepare
      val max = 2

      val props = FilteringActor.props(testActor, max)
      val filter = system.actorOf(props, "filter-1")

      //Execute
      filter ! Event(1)
      filter ! Event(2)
      filter ! Event(1)
      filter ! Event(3)

      //Verify

      /*
       * Test actor receives messages until they match a given partial function
       * Event(3) would no longer match
       *
       * Required because the filter actor is forwarding messages one-by-one (and not a list)
       */
      val eventIds = receiveWhile() {
        case Event(id) if id <= max => id
      }

      eventIds must be(List(1, 2))

      //the actor keeps receiving messages
      expectMsg(Event(3))
    }
  }

  "An Echo Actor" must {
    "respond with a copy of a request message" in {
      val echo = system.actorOf(Props[EchoActor])

      echo ! "Ales is happy!"

      //testActor is implicitly/automatically passed to the EchoActor
      expectMsg("Ales is happy!")
    }
  }
}
