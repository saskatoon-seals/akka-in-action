package lazyfsm.main

import java.util.concurrent.CompletableFuture

import org.scalatest.FunSuite

class EffectsExecutorTests extends FunSuite {
  val effectsExecutor = new EffectsExecutor(null, null);
  import effectsExecutor.performEffects;

  test("executeWorker:: (None, NoWorkerAction) => None") {
    assert(
      performEffects(List(), None) == None
    )
  }

  /*
   * It's very hard to test what exact thing the returned future is, so it's enough to just
   * say that it's Some(f) where f is future of any value.
   */
  test("performEffects():: (None, CreateWorker) => Some(future) - method forks a sub-task") {
    val subtask:Runnable = () => ()

    assert(
      performEffects(List(CreateWorker(subtask, List())), None) != None
    )
  }

  test("performEffects():: (Some(future), NoWorkerAction) => Some(future)") {
    val future = CompletableFuture.completedFuture(null: Void)

    assert(
      performEffects(List(), Some(future)) == None
    )
  }

  test("performEffects():: (Some(future), KillWorker) => None") {
    val future = CompletableFuture.completedFuture(null: Void)

    assert(
      performEffects(List(KillWorker), Some(future)) == None
    )
  }
}
