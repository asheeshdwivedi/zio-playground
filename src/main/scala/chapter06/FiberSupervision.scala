package chapter06

import zio._
import zio.Clock._
import zio.Console._
import scala.concurrent.duration._

object FiberSupervision:

  val child: ZIO[Has[Clock] with Has[Console], Nothing, Unit] =
    printLine("Child fiber beginning execution...").orDie *>
      ZIO.sleep(5.seconds) *>
      printLine("Hello from a child fiber!").orDie

  val parent: ZIO[Has[Clock] with Has[Console], Nothing, Unit] =
    printLine("Parent fiber beginning execution...").orDie *>
      child.fork *>
      ZIO.sleep(3.seconds) *>
      printLine(" Hello from parent fiber!").orDie

  val example: ZIO[Has[Clock] with Has[Console], Nothing, Unit] =
    for {
      fiber <- parent.fork
      _ <- ZIO.sleep(1.seconds)
      - <- fiber.interrupt
      _ <- ZIO.sleep(10.seconds)
    } yield ()

  val runtime = Runtime.default

  @main def run: Unit = runtime.unsafeRun(example)


