package akkainaction.fileupload

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import akkainaction.fileupload.parallel.Dispatcher
import akkainaction.tdd.StopSystemAfterAll
import org.scalatest.{MustMatchers, WordSpecLike}
import akkainaction.fileupload.Protocol._

class ParallelUploadsSuite extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with StopSystemAfterAll {

  val uri = "newFileUri"

  "The dispatcher" must {
    "create a worker actor and send it a start msg on request for a new file upload" in {
      val dispatcher = system.actorOf(Props[Dispatcher], "dispatcher")

      dispatcher ! Upload(uri)

      expectMsg(UploadStarted(uri))

      /*
       * More things to verify:
       *  1. How to assert that the new actor was created? -> access to "context"
       *  2. How to assert the message was sent to the newly created actor? -> mock "context.child(uri)"
       */
    }
  }
}
