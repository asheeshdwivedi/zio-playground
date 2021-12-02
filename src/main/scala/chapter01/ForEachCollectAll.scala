package chapter01

import zio.{ZIO, ZIOAppDefault}

object ForEachCollectAll extends ZIOAppDefault:

  //The foreach operator returns a single effect that describes performing an effect
  //for each element of a collection in sequence. It’s similar to a for loop in procedural
  //programming, which iterates over values, processes them in some fashion, and
  //collects the results.

  def printLine(line: String) = ZIO.attempt(println(line))
  val printNumber = ZIO.foreach(1 to 100){ n => printLine( n.toString) }

  //collectAll returns a single effect that collects the results of a whole
  //collection of effects. We could use this to collect the results of a number of
  //printing effects,

  val prints = List(
    printLine("The"),
    printLine("quick"),
    printLine("brown"),
    printLine("fox")
  )

  val printWords: ZIO[Any, Throwable, List[Unit]] = ZIO.collectAll(prints)

  override def run: ZIO[Any, Any, Any] = printWords.exitCode


