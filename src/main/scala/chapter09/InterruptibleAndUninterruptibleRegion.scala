package chapter09

import zio._


object InterruptibleAndUninterruptibleRegion:

  val runtime: Runtime[ZEnv] = Runtime.default

  val unintrruptible: UIO[Unit] = UIO(println("Doing something really importent!")).uninterruptible

  val intrruptible: UIO[Unit] = UIO(println("Feel free to intrrupte me if you want")).interruptible // this is meaningless ever effect is by default is interruptible

  val effect1: UIO[Boolean] = // effect can be intrrupted before setting up the uninterruptible status
    for
      ref <- Ref.make(false)
      fiber <- ref.set(true).uninterruptible.fork
      - <- fiber.interrupt
      value <- ref.get
    yield value

  val effect2: UIO[Boolean] =
    for
      ref <- Ref.make(false)
      fiber <- ref.set(true).fork.uninterruptible // At the time of fork the fiber inharit the interruptibility status from parant..
      // the parent executed the uninterruptible the child fiber  will also be uninterruptible so it is gurenteed to give true
      _ <- fiber.interrupt
      value <- ref.get
    yield value

  @main def runNotStableCode:Unit = println(runtime.unsafeRun(effect1.repeatN(10)))

  @main def runStableCode:Unit = println(runtime.unsafeRun(effect2.repeatN(10)))

  // Rules
  // - Combinators that change these setting apply to the entire scope they are invoked on
     // - (zio1 *> zio2 *> zio3).uninterruptible all three effects will be uninterruptible
  // - Inner scopes override outer scopes
    // - (zio1 *> zio2.interruptible *> zio3).uninterruptible zio2 would be interruptible  
  // - Forked fivers inherit the settings of their parent fiber at the time they are forked
