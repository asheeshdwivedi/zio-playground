package chapter08

import zio._
import zio.Clock._
import scala.concurrent.duration._

object HeartBeat:

  val runtime = Runtime.default

  def effect(scope: ZScope[Exit[Any, Any]]): ZIO[Has[Clock], Nothing, Int] =
    for
      - <- ZIO.succeed(println("heartbeat"))
        .delay(1.second)
        .forever
        .forkIn(scope)
      _ <- ZIO.succeed(println("Doing some expensive work"))
    yield 42

  val module =
    for
      fiber <- ZIO.forkScopeWith(scope => effect(scope).fork)
      _ <- ZIO.succeed(println("Doing some other work"))
        .delay(5.second)
      result <- fiber.join
    yield result

  val program =
    for
      fiber <- module.fork
      _ <- ZIO.succeed(println("Running another module entirely")).delay(10.second)
    yield ()

  @main def runheartBeat: Unit = println(runtime.unsafeRun(program *> ZIO.never))