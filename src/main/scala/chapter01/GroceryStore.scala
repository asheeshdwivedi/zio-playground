package chapter01
import zio._

object GroceryStore extends ZIOAppDefault:

  val goShopping = ZIO.attempt(println("Going to grocery store"))

  override def run  = goShopping.exitCode

  //def run(args: List[String]) = goShopping.exitCode
