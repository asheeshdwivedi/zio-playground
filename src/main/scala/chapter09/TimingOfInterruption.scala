package chapter09

import scala.io.StdIn
import zio._

object TimingOfInterruption:

  val runtime: Runtime[ZEnv] = Runtime.default
  // code as a blue print
  val greet: UIO[Unit] =
    for
      name <- ZIO.succeed(StdIn.readLine("What your's name?"))
      _ <- ZIO.succeed(println(s"Hello $name"))
    yield ()

  // Interruption before an effect begins Execution
  val effect1 =
    for
      fiber <- ZIO.succeed(println("Hello World!")).fork
      _ <- fiber.interrupt // Hello world may never get printed because fiber may get interrupted before it begin execution
    yield ()

  // finlizer will not run if effect not begin execution
  val effect2: UIO[Boolean] =
    for
      ref <- Ref.make(false)
      fiber <- ZIO.never.ensuring(ref.set(true)).fork
      _ <- fiber.interrupt // If interrupt ran before fiber begin execution finalizer will not run
      value <- ref.get
    yield value

  // guarantee finalizer to run
  val effect3: UIO[Boolean] =
    for
      ref <- Ref.make(false)
      promise <- Promise.make[Nothing, Unit]
      fiber <- (promise.succeed(()) *> ZIO.never)
        .ensuring(ref.set(true))
        .fork
      - <- promise.await // we are making sure the effect is begin execution before its been interrupt, i.e the finilizer will run in this case
      _ <- fiber.interrupt
      value <- ref.get
    yield value


  @main def runBluePrint: Unit = runtime.unsafeRun(greet)

  @main def runEffect1: Unit = runtime.unsafeRun(effect1)

  @main def renEffect2: Unit = println(runtime.unsafeRun(effect2))

  @main def renEffect3: Unit = println(runtime.unsafeRun(effect3))

  