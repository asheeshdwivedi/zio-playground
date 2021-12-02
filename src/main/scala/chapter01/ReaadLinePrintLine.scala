package chapter01

import zio.{ZIO, ZIOAppDefault}

import scala.io.StdIn

object ReaadLinePrintLine extends ZIOAppDefault:

  val readLine = ZIO.attempt(StdIn.readLine())

  def printLine(line: String) = ZIO.attempt(println(line))

  def run = for {
    line <- readLine
    _ <- printLine(line)
  } yield ()
