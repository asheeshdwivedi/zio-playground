package chapter09

import zio.*

import java.util.concurrent.atomic.AtomicBoolean

object InterruptionOfSideEffectCode:

  val runtime: Runtime[ZEnv] = Runtime.default

  val effect: UIO[Unit] = UIO.succeed {
    var i = 0
    while i < 1000 do
      println(i)
      i += 1
  }

  //effect will not be interruptible during execution. Interruption happens only between statements
  // in our example effect is single statement
  val sideEffectCode = for
    fiber <- effect.fork
    _ <- fiber.interrupt
  yield ()

  // Interrupt sideEffectCode attemptBlockingCancelable, says we aare importing an effect into ZIO that make take a long time
  // to complete execution and we know ZIO runtime does not know how to interrupt arbitrary code, so we are going to tell
  // it how by providing cancel action to run when interrupt
  def effect(canceled: AtomicBoolean): Task[Unit] =
    ZIO.attemptBlockingCancelable { // it actually interrupt the effect by actually interrupting the underlaying Thread
      var i = 0
      while i < 1000 && !canceled.get do
        println(i)
        i += 1
    } {
      UIO.succeed(canceled.set(true))
    }

  val effect1 =
    for
      ref <- ZIO.succeed(new AtomicBoolean(false))
      fiber <- effect(ref).fork
      _ <- fiber.interrupt
    yield ()


  @main def runEffect: Unit = println(runtime.unsafeRun(sideEffectCode))

  @main def runInterruptBlockingCode: Unit = println(runtime.unsafeRun(effect1))


