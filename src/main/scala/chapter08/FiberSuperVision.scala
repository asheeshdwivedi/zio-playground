package chapter08

import zio._
import zio.Clock._
import scala.concurrent.duration._

object FiberSuperVision:
  val runtime = Runtime.default

/*
  val grandChild: UIO[Unit] = ZIO.succeed(println("Hello, World!"))

  val child: UIO[Fiber[Nothing, Unit]] =  grandChild.fork

  val effect_1 = child.fork *> ZIO.never

  val grandChild_2: UIO[Unit] = ZIO.succeed(println("Hello, World!"))

  val child_2: UIO[Unit]  =  grandChild_2.fork.flatMap(_.join)

  val effect_2 = child_2.fork *> ZIO.never
*/

  val child_1: ZIO[Has[Clock], Nothing, Int] =
    for
      _ <- ZIO.succeed(println("hartbeat"))
        .delay(1.second)
        .forever
        .fork // this will get interpted as soon parent finishes since its running on parent scope
      - <- ZIO.succeed(println("Doing some expensive work"))
    yield 42

  val parent_1 =
    for
      fiber <- child_1.fork
      _ <- ZIO.succeed(println("Doing some other work"))
        .delay(5.second)
      result <- fiber.join
    yield result


  val child_2: ZIO[Has[Clock], Nothing, Int] =
    for
      _ <- ZIO.succeed(println("hartbeat"))
        .delay(1.second)
        .forever
        .forkDaemon // global scope not be interpted with the parent fiber finishes
      - <- ZIO.succeed(println("Doing some expensive work"))
    yield 42

  val parent_2 =
    for
      fiber <- child_2.fork
      _ <- ZIO.succeed(println("Doing some other work"))
        .delay(5.second)
      result <- fiber.join
    yield result


  // What if we want the heat beat fiber to needs to be run while certain part of the aplication is processing
  // not for the entire application

  val effect: ZIO[Has[Clock], Nothing, Int] =
    for
      -  <- ZIO.succeed(println("heartbeat"))
        .delay(1.second)
        .forever
        .forkDaemon
      _ <- ZIO.succeed(println("Doing some expensive work"))
    yield 42

  val module = // we wanted hearbeat to be sent only until module is running
    for
      fiber <- effect.fork
      - <- ZIO.succeed(println("Doing some other work"))
        .delay(5.second)
      result <- fiber.join
    yield result

  val program =
    for
      fiber <- module.fork
      _ <- ZIO.succeed(println("Running another module entirely"))
        .delay(10.second)
      _ <- fiber.join
    yield ()


  // heratbeat fiber foked it child_1 fiber as as soon the child_1 finishes it will interpret the heartbeat fiber
  @main def run_1: Unit = runtime.unsafeRun(parent_1 *> ZIO.never)

  // This will send heartbeat forverever
  @main def run_2: Unit = runtime.unsafeRun(parent_2 *> ZIO.never)
  
  //we want background heartbeat fiber to automatically interrupted when the module fiber completed
  // in program it not possible since the all the effect running in the scope of one fiber, we have to change the scope of the heartbeat 
  // effect to run at module fiber scope 
  @main def run_3: Unit = runtime.unsafeRun(program *> ZIO.never)
