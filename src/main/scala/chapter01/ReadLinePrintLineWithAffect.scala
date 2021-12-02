package chapter01

import zio.*

import java.io.IOException

object ReadLinePrintLineWithAffect extends ZIOAppDefault :

  val readInt: RIO[Has[Console], Int] = for {
    line <- Console.readLine
    int <- ZIO.attempt(line.toInt)
  } yield int

  lazy val readIntOrRetry: ZIO[Has[Console], IOException, Nothing] =
    readInt
      .orElse(Console.printLine("Please enter a valid interger"))
      .zipRight(readIntOrRetry)

  override def run =readIntOrRetry.exitCode


